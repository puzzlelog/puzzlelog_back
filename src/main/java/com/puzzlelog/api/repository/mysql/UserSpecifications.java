package com.puzzlelog.api.repository.mysql;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserSpecifications {

    // 조건별 동적 쿼리 생성 (Specification 조합)
    public static Specification<User> withConditions(UserSearchRequest req) {
        return Specification
            .where(emailEquals(req.getEmail()))
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

    private static Specification<User> emailEquals(String email) {
        return (root, query, cb) -> email == null ? null : cb.equal(root.get("email"), email);
    }

    private static Specification<User> userIdEquals(String userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    private static Specification<User> nicknameEquals(String nickname) {
        return (root, query, cb) -> nickname == null ? null : cb.equal(root.get("nickname"), nickname);
    }

    // 생성일자 범위로 조회
    private static Specification<User> createdAtBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;

        LocalDateTime fromDate = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toDate = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.MAX;

        return (root, query, cb) -> cb.between(root.get("createdAt"), fromDate, toDate);
    }

    // 생년월일 범위로 조회
    private static Specification<User> birthDateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;
        return (root, query, cb) -> cb.between(root.get("birthDate"), from, to);
    }

    private static Specification<User> genderEquals(String gender) {
        return (root, query, cb) -> gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    private static Specification<User> isAlarmEquals(Boolean isAlarm) {
        return (root, query, cb) -> isAlarm == null ? null : cb.equal(root.get("isAlarm"), isAlarm);
    }

    private static Specification<User> statusEquals(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<User> roleEquals(String role) {
        return (root, query, cb) -> role == null ? null : cb.equal(root.get("role"), role);
    }

    // 마지막 로그인 범위로 조회
    private static Specification<User> lastLoginBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;

        LocalDateTime fromDate = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toDate = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.MAX;

        return (root, query, cb) -> cb.between(root.get("lastLogin"), fromDate, toDate);
    }
}