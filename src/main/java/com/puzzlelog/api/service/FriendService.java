package com.puzzlelog.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.entity.Friend;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.response.friend.FriendDetailResponse;
import com.puzzlelog.api.dto.response.friend.FriendResponse;
import com.puzzlelog.api.dto.response.friend.PagedFriendResponse;
import com.puzzlelog.api.repository.mysql.FriendRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class FriendService {
    
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
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
        User requester = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("요청 사용자를 찾을 수 없습니다."));

        User receiver = userRepository.findByUserId(friendId)
            .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다."));

        validateUserStatus(requester, true);
        validateUserStatus(receiver, false);

        List<Friend> existingFriends = friendRepository.findAllByUser_UserIdAndFriend_UserId(userId, friendId);

        if (!existingFriends.isEmpty()) {
            Friend.Status status = existingFriends.get(0).getStatus();

            switch (status) {
                case PENDING:
                    throw new RuntimeException("이미 친구 요청한 상태입니다.");
                case ACCEPTED:
                    throw new RuntimeException("이미 친구입니다.");
                case BLOCKED:
                    throw new RuntimeException("차단된 사용자에게 친구 요청을 보낼 수 없습니다.");
                case DEACTIVATED:
                    existingFriends.get(0).setStatus(Friend.Status.PENDING);
                    friendRepository.save(existingFriends.get(0));
                    return FriendResponse.from(existingFriends.get(0));
                default:
                    throw new IllegalStateException("예상하지 못한 상태: " + status);
            }
        }

        Friend friend = Friend.builder()
            .user(requester)
            .friend(receiver)
            .status(Friend.Status.PENDING)
            .build();

        Friend savedFriend = friendRepository.save(friend);

        return FriendResponse.from(savedFriend);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(String userId, String friendId) {
        Friend friendRequest = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
                .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        if (friendRequest.getStatus() != Friend.Status.PENDING) {
            throw new RuntimeException("친구 요청이 이미 처리되었습니다.");
        }

        friendRequest.setStatus(Friend.Status.ACCEPTED);
        friendRepository.save(friendRequest);

        Friend reciprocalFriend = Friend.builder()
            .user(friendRequest.getFriend())
            .friend(friendRequest.getUser())
            .status(Friend.Status.ACCEPTED)
            .build();

        friendRepository.save(reciprocalFriend);
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

        Friend friend = friendRepository.findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElseGet(() -> Friend.builder()
                        .user(requester)
                        .friend(target)
                        .build());

        if (friend.getStatus() == Friend.Status.BLOCKED) {
            throw new RuntimeException("이미 차단된 사용자입니다.");
        }

        friend.setStatus(Friend.Status.BLOCKED);
        Friend savedFriend = friendRepository.save(friend);

        return FriendResponse.from(savedFriend);
    }

    // 친구 차단 해제
    @Transactional
    public FriendResponse unblockFriend(String userId, String friendId) {
        validateUserStatus(getUserByUserId(userId, true), true);
        validateUserStatus(getUserByUserId(friendId, false), false);

        Friend friend = friendRepository.findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElseThrow(() -> new RuntimeException("차단된 친구 정보를 찾을 수 없습니다."));

        if (friend.getStatus() != Friend.Status.BLOCKED) {
            throw new RuntimeException("현재 차단 상태가 아닙니다.");
        }

        friend.setStatus(Friend.Status.DEACTIVATED);
        Friend updatedFriend = friendRepository.save(friend);

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

        return FriendResponse.from(friend);
    }
}