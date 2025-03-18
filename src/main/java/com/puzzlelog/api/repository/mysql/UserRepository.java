package com.puzzlelog.api.repository.mysql;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;

public interface UserRepository extends JpaRepository<User, Integer> {
    
	Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    
    // 로그인 및 중복체크 시 사용
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    
    // 조회 사용
    @Query("SELECT u FROM User u WHERE "
            + "(:#{#req.email} IS NULL OR u.email = :#{#req.email}) AND "
            + "(:#{#req.userId} IS NULL OR u.userId = :#{#req.userId}) AND "
            + "(:#{#req.nickname} IS NULL OR u.nickname = :#{#req.nickname}) AND "
            + "(:#{#req.createdAtFrom} IS NULL OR u.createdAt >= :createdAtFrom) AND "
            + "(:#{#req.createdAtTo} IS NULL OR u.createdAt <= :createdAtTo) AND "
            + "(:#{#req.birthDateFrom} IS NULL OR u.birthDate >= :#{#req.birthDateFrom}) AND "
            + "(:#{#req.birthDateTo} IS NULL OR u.birthDate <= :#{#req.birthDateTo}) AND "
            + "(:#{#req.gender} IS NULL OR u.gender = :#{#req.gender}) AND "
            + "(:#{#req.isAlarm} IS NULL OR u.isAlarm = :#{#req.isAlarm}) AND "
            + "(:#{#req.status} IS NULL OR u.status = :#{#req.status}) AND "
            + "(:#{#req.role} IS NULL OR u.role = :#{#req.role}) AND "
            + "(:#{#req.lastLoginFrom} IS NULL OR u.lastLogin >= :lastLoginFrom) AND "
            + "(:#{#req.lastLoginTo} IS NULL OR u.lastLogin <= :lastLoginTo)")
        Page<User> findUsersByConditions(
            @Param("req") UserSearchRequest req,
            @Param("createdAtFrom") LocalDateTime createdAtFrom,
            @Param("createdAtTo") LocalDateTime createdAtTo,
            @Param("lastLoginFrom") LocalDateTime lastLoginFrom,
            @Param("lastLoginTo") LocalDateTime lastLoginTo,
            Pageable pageable);

        default Page<User> searchUsers(UserSearchRequest req, Pageable pageable) {
            return findUsersByConditions(
                req,
                req.getCreatedAtFrom() != null ? req.getCreatedAtFrom().atStartOfDay() : null,
                req.getCreatedAtTo() != null ? req.getCreatedAtTo().atTime(LocalTime.MAX) : null,
                req.getLastLoginFrom() != null ? req.getLastLoginFrom().atStartOfDay() : null,
                req.getLastLoginTo() != null ? req.getLastLoginTo().atTime(LocalTime.MAX) : null,
                pageable
            );
        }

}