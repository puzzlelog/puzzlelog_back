package com.puzzlelog.api.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.FriendHistory;
import com.puzzlelog.api.dao.entity.Friend;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.response.friend.FriendDetailResponse;
import com.puzzlelog.api.dto.response.friend.FriendResponse;
import com.puzzlelog.api.dto.response.friend.PagedFriendResponse;
import com.puzzlelog.api.repository.mongo.FriendHistoryRepository;
import com.puzzlelog.api.repository.mysql.FriendRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class FriendService {
    
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendHistoryRepository friendHistoryRepository;

    public FriendService(
    	    FriendRepository friendRepository,
    	    UserRepository userRepository,
    	    FriendHistoryRepository friendHistoryRepository
    	) {
    	    this.friendRepository = friendRepository;
    	    this.userRepository = userRepository;
    	    this.friendHistoryRepository = friendHistoryRepository;
    	}
    
    // 상태 확인 메소드
    private void validateUserStatus(User user, boolean isRequester) {
        if (user.getStatus() == User.Status.DELETED) {
            throw new RuntimeException(isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다.");
        } else if (user.getStatus() == User.Status.BANNED) {
            throw new RuntimeException(isRequester ? "차단된 사용자는 친구 요청을 보낼 수 없습니다."
                    : "차단된 사용자에게 친구 요청을 보낼 수 없습니다.");
        }
    }

    private User getUserByUserId(String userId, boolean isRequester) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다."));

        validateUserStatus(user, isRequester);

        return user;
    }

    // 친구 요청 보내기
    @Transactional
    public FriendResponse sendFriendRequest(String userId, String friendId) {
    	// 본인이 본인에게 친구 요청 방지
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다.");
        }
    	
        User requester = getUserByUserId(userId, true);
        User receiver = getUserByUserId(friendId, false);

        Friend existingFriend = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElse(null);

        Friend friend;

        if (existingFriend != null) {
            switch (existingFriend.getStatus()) {
                case PENDING:
                    throw new RuntimeException("이미 친구 요청한 상태입니다.");
                case ACCEPTED:
                    throw new RuntimeException("이미 친구입니다.");
                case BLOCKED:
                    throw new RuntimeException("차단된 사용자에게 친구 요청을 보낼 수 없습니다.");
                case DEACTIVATED:
                case REJECTED:
                    existingFriend.setStatus(Friend.Status.PENDING);
                    friend = friendRepository.save(existingFriend);
                    break;
                default:
                    throw new IllegalStateException("예상하지 못한 상태: " + existingFriend.getStatus());
            }
        } else {
            friend = Friend.builder()
                    .user(requester)
                    .friend(receiver)
                    .status(Friend.Status.PENDING)
                    .build();

            friend = friendRepository.save(friend);
        }

        // MongoDB에 친구 요청 상태 이력 저장
        FriendHistory history = FriendHistory.builder()
                .userId(userId)
                .friendId(friendId)
                .status(friend.getStatus().name())
                .timestamp(LocalDateTime.now())
                .build();

        friendHistoryRepository.save(history);

        return FriendResponse.from(friend);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(String userId, String friendId) {
        Friend friendRequest = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
                .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        if (friendRequest.getStatus() != Friend.Status.PENDING) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        friendRequest.setStatus(Friend.Status.ACCEPTED);
        friendRepository.save(friendRequest);

        Friend reciprocalFriend = Friend.builder()
            .user(friendRequest.getFriend())
            .friend(friendRequest.getUser())
            .status(Friend.Status.ACCEPTED)
            .build();

        friendRepository.save(reciprocalFriend);

        // ✅ 친구 수락 기록 MongoDB 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)              // 요청을 수락한 사람
            .friendId(friendId)          // 친구 요청 보낸 사람
            .status(Friend.Status.ACCEPTED.name())
            .timestamp(LocalDateTime.now())
            .build());
    }
    
    // 친구 요청 거절
    @Transactional
    public void rejectFriendRequest(String userId, String friendId) {
        Friend friendRequest = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
            .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        if (friendRequest.getStatus() != Friend.Status.PENDING) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        friendRequest.setStatus(Friend.Status.REJECTED);
        friendRepository.save(friendRequest);

        // ✅ 친구 요청 거절 기록 MongoDB 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)              // 요청을 거절한 사람
            .friendId(friendId)          // 친구 요청 보낸 사람
            .status(Friend.Status.REJECTED.name())
            .timestamp(LocalDateTime.now())
            .build());
    }

    // 친구 목록 조회 (페이징, 상태별 조회)
    @Transactional(readOnly = true)
    public PagedFriendResponse getFriendsByType(String userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Friend> friends;

        switch (type.toLowerCase()) {
            case "your_request":  // 나에게 온 친구 요청 (받은 요청)
                friends = friendRepository.findByFriend_UserIdAndStatus(userId, Friend.Status.PENDING, pageable);
                break;

            case "my_request":  // 내가 보낸 친구 요청
                friends = friendRepository.findByUser_UserIdAndStatus(userId, Friend.Status.PENDING, pageable);
                break;

            case "friends":  // 친구 상태
                friends = friendRepository.findByUser_UserIdAndStatus(userId, Friend.Status.ACCEPTED, pageable);
                break;

            case "blocked":  // 차단된 친구 목록
                friends = friendRepository.findByUser_UserIdAndStatus(userId, Friend.Status.BLOCKED, pageable);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 조회 타입입니다: " + type);
        }

        Page<FriendDetailResponse> friendDetails = friends.map(friend -> FriendDetailResponse.builder()
            .userId(userId)
            .friendId(type.equals("your_request") ? friend.getUser().getUserId() : friend.getFriend().getUserId())
            .nickname(type.equals("your_request") ? friend.getUser().getNickname() : friend.getFriend().getNickname())
            .updatedAt(friend.getUpdatedAt().toString())
            .build()
        );

        return PagedFriendResponse.from(friendDetails);
    }
    
    // 친구 차단
    @Transactional
    public FriendResponse blockFriend(String userId, String friendId) {
        User requester = getUserByUserId(userId, true);
        User target = getUserByUserId(friendId, false);

        Friend friend = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
            .orElseGet(() -> Friend.builder()
                    .user(requester)
                    .friend(target)
                    .build());

        friend.setStatus(Friend.Status.BLOCKED);
        friendRepository.save(friend);

        // MongoDB에 차단 상태 기록 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)
            .friendId(friendId)
            .status(Friend.Status.BLOCKED.name())
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(friend);
    }

    // 친구 차단 해제
    @Transactional
    public FriendResponse unblockFriend(String userId, String friendId) {
        validateUserStatus(getUserByUserId(userId, true), true);
        validateUserStatus(getUserByUserId(friendId, false), false);

        Friend friend = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElseThrow(() -> new RuntimeException("차단된 친구 정보를 찾을 수 없습니다."));

        if (friend.getStatus() != Friend.Status.BLOCKED) {
            throw new RuntimeException("현재 차단 상태가 아닙니다.");
        }

        // 차단 해제 시 기존 친구 상태로 복원 (ACCEPTED)
        friend.setStatus(Friend.Status.ACCEPTED);
        Friend updatedFriend = friendRepository.save(friend);

        // MongoDB에 차단 해제 상태 기록 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)
            .friendId(friendId)
            .status(Friend.Status.ACCEPTED.name()) // 차단 해제 시 ACCEPTED 상태로 복귀
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(updatedFriend);
    }
    
    // 친구 삭제
    @Transactional
    public FriendResponse deactivateFriend(String userId, String friendId) {
        validateUserStatus(getUserByUserId(userId, true), true);
        validateUserStatus(getUserByUserId(friendId, false), false);

        Friend friend = friendRepository.findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElseThrow(() -> new RuntimeException("친구가 아닙니다."));

        if (friend.getStatus() != Friend.Status.ACCEPTED) {
            throw new RuntimeException("친구가 아닙니다.");
        }

        friend.setStatus(Friend.Status.DEACTIVATED);
        friendRepository.save(friend);

        friendRepository.findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
                .ifPresent(reciprocal -> {
                    reciprocal.setStatus(Friend.Status.DEACTIVATED);
                    friendRepository.save(reciprocal);
                });

        // 친구 삭제 기록 MongoDB 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)     // 삭제한 사람
            .friendId(friendId) // 삭제된 상대방
            .status(Friend.Status.DEACTIVATED.name())
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(friend);
    }
}