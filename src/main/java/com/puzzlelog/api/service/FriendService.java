package com.puzzlelog.api.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.entity.Friend;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.response.FriendResponse;
import com.puzzlelog.api.dto.response.PagedFriendResponse;
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
            String message = isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다.";
            throw new RuntimeException(message);
        } else if (user.getStatus() == User.Status.BANNED) {
            String message = isRequester ? "차단된 사용자는 친구 요청을 보낼 수 없습니다."
                                          : "차단된 사용자에게 친구 요청을 보낼 수 없습니다.";
            throw new RuntimeException(message);
        }
    }
    
    // DELETED 체크
    private void validateUserExists(User user, boolean isRequester) {
        if (user.getStatus() == User.Status.DELETED) {
            String message = isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다.";
            throw new RuntimeException(message);
        }
    }

    // 친구 요청 보내기
    @Transactional
    public FriendResponse sendFriendRequest(int userId, int friendId) {
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("요청 사용자를 찾을 수 없습니다."));

        User receiver = userRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다."));

        validateUserStatus(requester, true);
        validateUserStatus(receiver, false);

        // 중복된 데이터가 존재할 수 있으므로 List로 변경
        List<Friend> existingFriends = friendRepository.findAllByUserIdAndFriendId(userId, friendId);

        if (!existingFriends.isEmpty()) {
            // 중복 데이터가 있다면 가장 최근 상태를 확인
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

        // 기존 요청 없을 경우 새 요청 생성
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
    public void acceptFriendRequest(int userId, int friendId) {
        Friend friendRequest = friendRepository
                .findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(friendId, userId)
                .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        if (friendRequest.getStatus() != Friend.Status.PENDING) {
            throw new RuntimeException("친구 요청이 이미 처리되었습니다.");
        }

        friendRequest.setStatus(Friend.Status.ACCEPTED);
        friendRepository.save(friendRequest);

        // 양방향 관계 데이터 추가
        Friend reciprocalFriend = Friend.builder()
            .user(friendRequest.getFriend())
            .friend(friendRequest.getUser())
            .status(Friend.Status.ACCEPTED)
            .build();

        friendRepository.save(reciprocalFriend);
    }

    // 친구 목록 조회 (페이징, 상태별 조회)
    @Transactional(readOnly = true)
    public PagedFriendResponse getFriendsByType(int userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Friend> friends;

        switch (type.toLowerCase()) {
            case "received":  // 내가 받은 친구 요청 (상태가 PENDING)
                friends = friendRepository.findByFriendIdAndStatus(userId, Friend.Status.PENDING, pageable);
                break;
            case "sent":  // 내가 보낸 요청 중 아직 PENDING 상태
                friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.PENDING, pageable);
                break;
            case "friends":  // 친구 상태
                friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.ACCEPTED, pageable);
                break;
            case "blocked":  // 차단된 친구 목록
                friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.BLOCKED, pageable);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 조회 타입입니다: " + type);
        }

        return PagedFriendResponse.from(friends);
    }
    
    // 친구 차단
    @Transactional
    public FriendResponse blockFriend(int userId, int friendId) {
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("요청 사용자를 찾을 수 없습니다."));
        User target = userRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다."));

        validateUserExists(requester, true);
        validateUserExists(target, false);

        Friend friend = friendRepository.findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(userId, friendId)
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
    public FriendResponse unblockFriend(int userId, int friendId) {
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("요청 사용자를 찾을 수 없습니다."));
        User target = userRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다."));

        validateUserExists(requester, true);
        validateUserExists(target, false);
        
        // Friend 테이블에 두 사용자 간의 관계가 전혀 존재하지 않는 경우 (즉, 사용자는 있지만 관계는 없는 경우)
        Friend friend = friendRepository.findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(userId, friendId)
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
    public FriendResponse deactivateFriend(int userId, int friendId) {
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("요청 사용자를 찾을 수 없습니다."));

        User target = userRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다."));

        validateUserStatus(requester, true);
        validateUserStatus(target, false);

        Friend friend = friendRepository.findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(userId, friendId)
                .orElseThrow(() -> new RuntimeException("친구가 아닙니다."));

        if (friend.getStatus() != Friend.Status.ACCEPTED) {
            throw new RuntimeException("친구가 아닙니다.");
        }

        friend.setStatus(Friend.Status.DEACTIVATED);
        friendRepository.save(friend);

        // 양방향 관계도 동일 처리
        friendRepository.findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(friendId, userId)
                .ifPresent(reciprocal -> {
                    reciprocal.setStatus(Friend.Status.DEACTIVATED);
                    friendRepository.save(reciprocal);
                });

        return FriendResponse.from(friend);
    }
}