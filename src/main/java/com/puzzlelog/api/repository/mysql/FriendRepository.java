package com.puzzlelog.api.repository.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.puzzlelog.api.dao.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    // 특정 상태의 친구 관계 존재 여부 확인 (PENDING, ACCEPTED 등)
    boolean existsByUser_UserIdAndFriend_UserIdAndStatusIn(String userId, String friendId, Friend.Status... statuses);

    // 특정 친구 관계 조회 (중복까지 방지)
    List<Friend> findAllByUser_UserIdAndFriend_UserId(String userId, String friendId);

    // 특정 친구 관계 조회 (단건)
    Optional<Friend> findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(String userId, String friendId);

    // 친구 요청 받은 목록 조회 (상태가 PENDING이며 friendId가 특정 사용자)
    List<Friend> findAllByFriend_UserIdAndStatus(String friendId, Friend.Status status);

    // 친구 요청 보낸 목록 조회 (상태가 PENDING이며 userId가 특정 사용자)
    List<Friend> findAllByUser_UserIdAndStatus(String userId, Friend.Status status);

    // 내가 받은 친구 요청 (상태가 PENDING)
    Page<Friend> findByFriend_UserIdAndStatus(String friendId, Friend.Status status, Pageable pageable);

    // 내가 보낸 친구 요청 (상태가 PENDING), 친구 상태 ACCEPTED 조회
    Page<Friend> findByUser_UserIdAndStatus(String userId, Friend.Status status, Pageable pageable);
}
