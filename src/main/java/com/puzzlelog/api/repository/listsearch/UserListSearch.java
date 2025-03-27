package com.puzzlelog.api.repository.listsearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;

/**
 * 사용자 목록 검색 조건 빌더
 * UserSearchRequest 기반으로 JPA Specification을 생성하여 동적 검색 조건을 구성합니다.
 */
@Component
public class UserListSearch implements ListSearch<UserSearchRequest, Specification<User>> {

    /**
     * 전체 검색 조건 조합 메서드
     * - 기본적으로 status != DELETED 필터를 포함합니다.
     * - null인 조건은 무시됩니다.
     */
    @Override
    public Specification<User> buildSearch(UserSearchRequest req) {
        return Specification.where(notDeleted())
            .and(emailEquals(req.getEmail()))
            .and(userIdEquals(req.getUserId()))
            .and(nicknameEquals(req.getNickname()))
            .and(createdAtBetween(req.getCreatedAtFrom(), req.getCreatedAtTo()))
            .and(birthDateBetween(req.getBirthDateFrom(), req.getBirthDateTo()))
            .and(genderEquals(req.getGender()))
            .and(isAlarmEquals(req.getIsAlarm()))
            .and(statusEquals(req.getStatus()))
            .and(roleEquals(req.getRole()))
            .and(lastLoginBetween(req.getLastLoginFrom(), req.getLastLoginTo()));
    }

    /** status != DELETED 필터 */
    private Specification<User> notDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), "DELETED");
    }

    private Specification<User> emailEquals(String email) {
        return (root, query, cb) -> email == null ? null : cb.equal(root.get("email"), email);
    }

    private Specification<User> userIdEquals(String userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    private Specification<User> nicknameEquals(String nickname) {
        return (root, query, cb) -> nickname == null ? null : cb.equal(root.get("nickname"), nickname);
    }

    private Specification<User> createdAtBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;

        LocalDateTime fromDate = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toDate = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.MAX;

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("createdAtFrom 날짜는 createdAtTo 날짜보다 이전이어야 합니다.");
        }

        return (root, query, cb) -> cb.between(root.get("createdAt"), fromDate, toDate);
    }

    private Specification<User> birthDateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("birthDateFrom 날짜는 birthDateTo 날짜보다 이전이어야 합니다.");
        }

        LocalDate fromDate = from != null ? from : LocalDate.of(1900, 1, 1);
        LocalDate toDate = to != null ? to : LocalDate.now();

        return (root, query, cb) -> cb.between(root.get("birthDate"), fromDate, toDate);
    }

    /**
     * 성별 필터
     * - MALE, FEMALE만 허용
     * - "null" 문자열은 gender IS NULL을 의미합니다.
     */
    private Specification<User> genderEquals(String gender) {
        if (gender == null) return null;

        if ("null".equalsIgnoreCase(gender)) {
            return (root, query, cb) -> cb.isNull(root.get("gender"));
        }

        if (!Set.of("MALE", "FEMALE").contains(gender)) {
            throw new IllegalArgumentException("유효하지 않은 성별(gender) 값입니다: " + gender);
        }

        return (root, query, cb) -> cb.equal(root.get("gender"), gender);
    }

    private Specification<User> isAlarmEquals(Boolean isAlarm) {
        return (root, query, cb) -> isAlarm == null ? null : cb.equal(root.get("isAlarm"), isAlarm);
    }

    /** 상태 필터 (ACTIVE, BANNED만 허용) */
    private Specification<User> statusEquals(String status) {
        if (status == null) return null;

        if (!Set.of("ACTIVE", "BANNED").contains(status)) {
            throw new IllegalArgumentException("잘못된 상태(status) 값입니다: " + status);
        }

        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /** 권한 필터 (USER, ADMIN만 허용) */
    private Specification<User> roleEquals(String role) {
        if (role == null) return null;

        if (!Set.of("USER", "ADMIN").contains(role)) {
            throw new IllegalArgumentException("유효하지 않은 권한(role) 값입니다: " + role);
        }

        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    private Specification<User> lastLoginBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;

        LocalDateTime fromDate = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toDate = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.MAX;

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("lastLoginFrom 날짜는 lastLoginTo 날짜보다 이전이어야 합니다.");
        }

        return (root, query, cb) -> cb.between(root.get("lastLogin"), fromDate, toDate);
    }
}