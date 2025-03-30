package com.puzzlelog.api.repository.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.puzzlelog.api.dao.entity.Friend;

/**
 * 친구 관계에 대한 JPA Repository입니다.
 * 
 * - userId는 친구 요청을 보낸 사람
 * - friendId는 친구 요청을 받은 사람
 * 
 * PENDING, ACCEPTED, BLOCKED, DEACTIVATED, REJECTED 상태를 기준으로
 * 쌍방향 친구 관계를 관리합니다.
 */
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    /**
     * 특정 상태 중 하나에 해당하는 친구 관계가 존재하는지 확인합니다.
     * 주로 중복 요청 방지에 사용됩니다.
     */
    boolean existsByUser_UserIdAndFriend_UserIdAndStatusIn(String userId, String friendId, String... statuses);

    /**
     * 두 사용자 간의 모든 친구 관계를 조회합니다.
     * 상태 상관 없이 모든 이력 포함 (관리자/내부용).
     */
    List<Friend> findAllByUser_UserIdAndFriend_UserId(String userId, String friendId);

    /**
     * 두 사용자 간의 가장 최신 친구 관계를 단건 조회합니다.
     * 상태에 따라 가장 최근 요청이나 상태 변경을 확인할 때 사용합니다.
     */
    Optional<Friend> findFirstByUser_UserIdAndFriend_UserIdOrderByCreatedAtDesc(String userId, String friendId);

    /**
     * 내가 받은 친구 요청 목록 (특정 상태)
     * ex. PENDING 요청만 조회
     */
    List<Friend> findAllByFriend_UserIdAndStatus(String friendId, String status);

    /**
     * 내가 보낸 친구 요청 목록 (특정 상태)
     * ex. 내가 보낸 PENDING 상태 요청들
     */
    List<Friend> findAllByUser_UserIdAndStatus(String userId, String status);

    /**
     * 내가 받은 친구 요청 목록 (페이징)
     */
    Page<Friend> findByFriend_UserIdAndStatus(String friendId, String status, Pageable pageable);

    /**
     * 내가 보낸 친구 요청 목록 (또는 친구 상태 목록, 페이징)
     */
    Page<Friend> findByUser_UserIdAndStatus(String userId, String status, Pageable pageable);
}
