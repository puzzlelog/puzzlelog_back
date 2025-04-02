package com.puzzlelog.api.repository.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.puzzlelog.api.dao.entity.User;

/**
 * 사용자 JPA Repository 인터페이스
 * 
 * MySQL 기반 사용자 엔티티에 대한 CRUD 및 동적 조건 검색 기능을 제공합니다.
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * 사용자 ID (로그인 ID)로 사용자 조회
     *
     * @param userId 사용자 로그인 ID
     * @return 해당 사용자 정보 (Optional)
     */
    Optional<User> findByUserId(String userId);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 사용자 이메일
     * @return 해당 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자 조회
     *
     * @param nickname 사용자 닉네임
     * @return 해당 사용자 정보 (Optional)
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 주어진 사용자 ID가 존재하는지 여부 확인
     *
     * @param userId 사용자 로그인 ID
     * @return 중복 여부
     */
    boolean existsByUserId(String userId);

    /**
     * 주어진 이메일이 존재하는지 여부 확인
     *
     * @param email 사용자 이메일
     * @return 중복 여부
     */
    boolean existsByEmail(String email);

    /**
     * 주어진 닉네임이 존재하는지 여부 확인
     *
     * @param nickname 사용자 닉네임
     * @return 중복 여부
     */
    boolean existsByNickname(String nickname);
    
    List<User> findByUserIdIn(List<String> userIds);
}
