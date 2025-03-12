package com.puzzlelog.api.repository.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.puzzlelog.api.dao.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    // 특정 상태의 친구 관계 존재 여부 확인 (PENDING, ACCEPTED 등)
    boolean existsByUserIdAndFriendIdAndStatusIn(Integer userId, Integer friendId, Friend.Status... statuses);

    // 특정 친구 관계 조회 (중복까지 방지)
    List<Friend> findAllByUserIdAndFriendId(int userId, int friendId);
    
    // 특정 친구 관계 조회 (단건)
    Optional<Friend> findFirstByUserIdAndFriendIdOrderByCreatedAtDesc(int userId, int friendId);
    
    // 친구 요청 받은 목록 조회 (상태가 PENDING이며 friendId가 특정 사용자)
    List<Friend> findAllByFriendIdAndStatus(int friendId, Friend.Status status);

    // 친구 요청 보낸 목록 조회 (상태가 PENDING이며 userId가 특정 사용자)
    List<Friend> findAllByUserIdAndStatus(int userId, Friend.Status status);
    
    // 내가 받은 친구 요청 (상태가 PENDING)
    Page<Friend> findByFriendIdAndStatus(Integer friendId, Friend.Status status, Pageable pageable);

    // 내가 보낸 친구 요청 (상태가 PENDING), 친구 상태 ACCEPTED 조회
    Page<Friend> findByUserIdAndStatus(Integer userId, Friend.Status status, Pageable pageable);
}
