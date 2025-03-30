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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendHistoryRepository friendHistoryRepository;

    /**
     * 사용자 상태를 검증합니다.
     *
     * - DELETED: 탈퇴한 계정은 사용할 수 없습니다.
     * - BANNED: 정지된 사용자는 친구 기능 제한
     *
     * @param user 검사할 사용자
     * @param isRequester 요청자인 경우 true, 수신자인 경우 false
     * @throws RuntimeException 유효하지 않은 사용자일 경우 예외 발생
     */
    private void validateUserStatus(User user, boolean isRequester) {
        if ("DELETED".equals(user.getStatus())) {
            throw new RuntimeException(isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다.");
        } else if ("BANNED".equals(user.getStatus())) {
            throw new RuntimeException(isRequester ? "차단된 사용자는 친구 요청을 보낼 수 없습니다."
                    : "차단된 사용자에게 친구 요청을 보낼 수 없습니다.");
        }
    }

    /**
     * userId를 기반으로 사용자 정보를 조회하고 상태를 검증합니다.
     *
     * @param userId 조회할 사용자 ID
     * @param isRequester 요청자인 경우 true, 수신자인 경우 false
     * @return 유효한 사용자 엔티티
     * @throws RuntimeException 존재하지 않거나 유효하지 않은 사용자일 경우
     */
    private User getUserByUserId(String userId, boolean isRequester) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(isRequester ? "요청 사용자를 찾을 수 없습니다." : "대상 사용자를 찾을 수 없습니다."));

        validateUserStatus(user, isRequester);

        return user;
    }

    /**
     * 친구 요청을 보냅니다.
     * 이미 존재하는 요청에 대해 상태에 따라 처리되며,
     * MongoDB에 요청 이력이 기록됩니다.
     *
     * @param userId   요청자 (본인)의 사용자 ID
     * @param friendId 요청 대상자의 사용자 ID
     * @return 생성 또는 갱신된 친구 요청 정보
     */
    @Transactional
    public FriendResponse sendFriendRequest(String userId, String friendId) {
        // 본인에게 친구 요청 방지
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다.");
        }

        // 요청자와 수신자 유효성 검증
        User requester = getUserByUserId(userId, true);
        User receiver = getUserByUserId(friendId, false);

        // 기존 친구 요청이 존재하는지 확인
        Friend existingFriend = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElse(null);

        Friend friend;

        if (existingFriend != null) {
            // 기존 상태에 따라 예외 또는 갱신
            switch (existingFriend.getStatus()) {
                case "PENDING":
                    throw new RuntimeException("이미 친구 요청한 상태입니다.");
                case "ACCEPTED":
                    throw new RuntimeException("이미 친구입니다.");
                case "BLOCKED":
                    throw new RuntimeException("차단된 사용자에게 친구 요청을 보낼 수 없습니다.");
                case "DEACTIVATED":
                case "REJECTED":
                    existingFriend.setStatus("PENDING");
                    friend = friendRepository.save(existingFriend);
                    break;
                default:
                    throw new IllegalStateException("예상하지 못한 상태: " + existingFriend.getStatus());
            }
        } else {
            // 새 친구 요청 생성
            friend = Friend.builder()
                    .user(requester)
                    .friend(receiver)
                    .status("PENDING")
                    .build();
            friend = friendRepository.save(friend);
        }

        // 요청 이력 MongoDB 기록
        FriendHistory history = FriendHistory.builder()
                .userId(userId)
                .friendId(friendId)
                .status(friend.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        friendHistoryRepository.save(history);

        return FriendResponse.from(friend);
    }

    /**
     * 친구 요청을 수락합니다.
     * 수신자(userId)가 보낸 요청(friendId)을 수락하며,
     * 양방향 친구 관계를 저장하고, 수락 이력을 MongoDB에 기록합니다.
     *
     * @param userId   수신자 (현재 로그인한 사용자 ID)
     * @param friendId 친구 요청을 보낸 사용자 ID
     */
    @Transactional
    public void acceptFriendRequest(String userId, String friendId) {
        // B → A 요청 찾기
        Friend friendRequest = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
                .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        if (!"PENDING".equals(friendRequest.getStatus())) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        // 요청 방향 업데이트
        friendRequest.setStatus("ACCEPTED");
        friendRepository.save(friendRequest);

        // 반대 방향(A → B)이 이미 존재하는 경우 상태 갱신, 없으면 새로 생성
        Friend reciprocalFriend = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
            .map(existing -> {
                existing.setStatus("ACCEPTED");
                return existing;
            })
            .orElse(Friend.builder()
                .user(friendRequest.getFriend())   // userId
                .friend(friendRequest.getUser())   // friendId
                .status("ACCEPTED")
                .build());

        friendRepository.save(reciprocalFriend);

        // 수락 이력 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)
            .friendId(friendId)
            .status("ACCEPTED")
            .timestamp(LocalDateTime.now())
            .build());
    }
    
    /**
     * 친구 요청을 거절합니다.
     * 수신자(userId)가 보낸 요청(friendId)을 거절하고,
     * 해당 요청의 상태를 REJECTED로 변경하며, 이력을 MongoDB에 기록합니다.
     *
     * @param userId   현재 로그인한 사용자 ID (요청을 거절하는 사람)
     * @param friendId 친구 요청을 보낸 사용자 ID
     */
    @Transactional
    public void rejectFriendRequest(String userId, String friendId) {
        // 요청자가 friendId이고, 수신자가 userId인 요청을 조회
        Friend friendRequest = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
            .orElseThrow(() -> new RuntimeException("해당 친구 요청을 찾을 수 없습니다."));

        // 수락 가능한 상태인지 확인
        if (!"PENDING".equals(friendRequest.getStatus())) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        // 상태를 REJECTED로 변경
        friendRequest.setStatus("REJECTED");
        friendRepository.save(friendRequest);

        // MongoDB에 친구 요청 거절 이력 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)              // 거절한 사람
            .friendId(friendId)          // 요청한 사람
            .status("REJECTED")
            .timestamp(LocalDateTime.now())
            .build());
    }


    /**
     * 친구 목록을 유형별로 조회합니다.
     * 
     * - your_request: 나에게 온 친구 요청 목록
     * - my_request: 내가 보낸 친구 요청 목록
     * - friends: 친구로 수락된 목록
     * - blocked: 내가 차단한 친구 목록
     *
     * @param userId 조회할 사용자 ID (JWT에서 추출됨)
     * @param type 친구 목록 조회 타입
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 당 항목 수
     * @return 페이징된 친구 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public PagedFriendResponse getFriendsByType(String userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Friend> friends;

        // type 파라미터에 따라 적절한 친구 목록을 조회
        switch (type.toLowerCase()) {
            case "your_request":  // 받은 친구 요청 (상대방이 나에게 요청한 경우)
                friends = friendRepository.findByFriend_UserIdAndStatus(userId, "PENDING", pageable);
                break;

            case "my_request":  // 보낸 친구 요청 (내가 보낸 요청)
                friends = friendRepository.findByUser_UserIdAndStatus(userId, "PENDING", pageable);
                break;

            case "friends":  // 친구 목록
                friends = friendRepository.findByUser_UserIdAndStatus(userId, "ACCEPTED", pageable);
                break;

            case "blocked":  // 내가 차단한 친구 목록
                friends = friendRepository.findByUser_UserIdAndStatus(userId, "BLOCKED", pageable);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 조회 타입입니다: " + type);
        }

        // Friend 엔티티 → FriendDetailResponse DTO로 매핑
        Page<FriendDetailResponse> friendDetails = friends.map(friend -> {
            boolean isIncomingRequest = type.equalsIgnoreCase("your_request");
            return FriendDetailResponse.builder()
                    .userId(userId)
                    .friendId(isIncomingRequest ? friend.getUser().getUserId() : friend.getFriend().getUserId())
                    .nickname(isIncomingRequest ? friend.getUser().getNickname() : friend.getFriend().getNickname())
                    .updatedAt(friend.getUpdatedAt().toString())
                    .build();
        });

        return PagedFriendResponse.from(friendDetails);
    }
    
    /**
     * 특정 사용자를 차단합니다.
     * 기존 친구 요청이나 친구 관계가 존재하더라도,
     * 무조건 BLOCKED 상태로 설정하며, 차단 이력을 MongoDB에 기록합니다.
     *
     * @param userId   현재 로그인한 사용자 ID (차단하는 사람)
     * @param friendId 차단 대상 사용자 ID
     * @return 차단된 친구 관계 정보
     */
    @Transactional
    public FriendResponse blockFriend(String userId, String friendId) {
        // 사용자 유효성 검사
        User requester = getUserByUserId(userId, true);   // 차단하는 사람
        User target = getUserByUserId(friendId, false);   // 차단당하는 사람

        // 기존 친구 관계가 있으면 가져오고, 없으면 새로 생성
        Friend friend = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
            .orElseGet(() -> Friend.builder()
                    .user(requester)
                    .friend(target)
                    .build());

        // 상태를 BLOCKED로 설정
        friend.setStatus("BLOCKED");
        friendRepository.save(friend);

        // MongoDB에 차단 이력 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)     // 차단한 사람
            .friendId(friendId) // 차단당한 사람
            .status("BLOCKED")
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(friend);
    }

    /**
     * 친구 차단을 해제합니다.
     * 차단 상태였던 친구 관계를 다시 ACCEPTED 상태로 복원하고,
     * MongoDB에 이력을 저장합니다.
     *
     * @param userId   현재 로그인한 사용자 ID (차단 해제하는 사람)
     * @param friendId 차단 해제할 대상 사용자 ID
     * @return 갱신된 친구 관계 정보
     */
    @Transactional
    public FriendResponse unblockFriend(String userId, String friendId) {
        // 사용자 상태 유효성 확인 (둘 다 ACTIVE 상태여야 함)
        validateUserStatus(getUserByUserId(userId, true), true);
        validateUserStatus(getUserByUserId(friendId, false), false);

        // 차단 상태의 친구 관계 조회
        Friend friend = friendRepository
                .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
                .orElseThrow(() -> new RuntimeException("차단된 친구 정보를 찾을 수 없습니다."));

        // 현재 BLOCKED 상태인지 확인
        if (!"BLOCKED".equals(friend.getStatus())) {
            throw new RuntimeException("현재 차단 상태가 아닙니다.");
        }

        // 차단 해제 → 다시 ACCEPTED 상태로 복구
        friend.setStatus("ACCEPTED");
        Friend updatedFriend = friendRepository.save(friend);

        // MongoDB에 차단 해제 이력 저장
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)
            .friendId(friendId)
            .status("ACCEPTED") // 해제 후 상태는 다시 친구로 간주
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(updatedFriend);
    }
    
    /**
     * 친구를 삭제(비활성화)합니다.
     * 친구 상태가 ACCEPTED인 경우에만 삭제가 가능하며,
     * 양방향 관계를 모두 DEACTIVATED 상태로 변경합니다.
     * 삭제 이력은 MongoDB에 저장됩니다.
     *
     * @param userId   현재 로그인한 사용자 ID (삭제 요청자)
     * @param friendId 삭제할 친구의 사용자 ID
     * @return 삭제된 친구 관계 정보
     */
    @Transactional
    public FriendResponse deactivateFriend(String userId, String friendId) {
        // 사용자 상태 유효성 확인
        validateUserStatus(getUserByUserId(userId, true), true);   // 본인
        validateUserStatus(getUserByUserId(friendId, false), false); // 친구 대상

        // 나 → 친구 관계 조회 (정방향)
        Friend friend = friendRepository
            .findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(userId, friendId)
            .orElseThrow(() -> new RuntimeException("친구가 아닙니다."));

        // 친구 상태가 ACCEPTED가 아닐 경우 삭제 불가
        if (!"ACCEPTED".equals(friend.getStatus())) {
            throw new RuntimeException("친구가 아닙니다.");
        }

        // 친구 상태 → DEACTIVATED 처리
        friend.setStatus("DEACTIVATED");
        friendRepository.save(friend);

        // 친구 → 나 관계도 DEACTIVATED 처리 (역방향)
        friendRepository.findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(friendId, userId)
            .ifPresent(reciprocal -> {
                reciprocal.setStatus("DEACTIVATED");
                friendRepository.save(reciprocal);
            });

        // MongoDB에 삭제 이력 기록
        friendHistoryRepository.save(FriendHistory.builder()
            .userId(userId)     // 삭제 요청자
            .friendId(friendId) // 삭제 대상
            .status("DEACTIVATED")
            .timestamp(LocalDateTime.now())
            .build());

        return FriendResponse.from(friend);
    }
}