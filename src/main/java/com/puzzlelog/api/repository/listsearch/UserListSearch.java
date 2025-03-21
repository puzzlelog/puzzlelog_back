package com.puzzlelog.api.repository.listsearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;

@Component
public class UserListSearch implements ListSearch<UserSearchRequest, Specification<User>> { // 유저 목록 조회

    @Override
    public Specification<User> buildSearch(UserSearchRequest req) {
        return Specification.where(emailEquals(req.getEmail()))
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

    private Specification<User> genderEquals(String gender) {
        return (root, query, cb) -> gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    private Specification<User> isAlarmEquals(Boolean isAlarm) {
        return (root, query, cb) -> isAlarm == null ? null : cb.equal(root.get("isAlarm"), isAlarm);
    }

    private Specification<User> statusEquals(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private Specification<User> roleEquals(String role) {
        return (root, query, cb) -> role == null ? null : cb.equal(root.get("role"), role);
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
