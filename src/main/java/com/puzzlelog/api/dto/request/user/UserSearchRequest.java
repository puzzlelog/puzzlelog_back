package com.puzzlelog.api.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 최대 100자까지 입력할 수 있습니다.")
    private String email;

    @Size(max = 50, message = "아이디는 최대 50자까지 입력할 수 있습니다.")
    private String userId;

    @Size(max = 50, message = "닉네임은 최대 50자까지 입력할 수 있습니다.")
    private String nickname;

    @Pattern(regexp = "^(MALE|FEMALE)?$", message = "성별은 MALE, FEMALE 또는 입력하지 않아야 합니다.")
    private String gender;

    private Boolean isAlarm;

    @Pattern(regexp = "ACTIVE|BANNED", message = "상태는 ACTIVE 또는 BANNED 중 하나여야 합니다.")
    private String status;

    @Pattern(regexp = "USER|ADMIN", message = "권한은 USER 또는 ADMIN 중 하나여야 합니다.")
    private String role;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastLoginFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastLoginTo;

    public boolean hasNoCondition() {
        return Stream.of(
                email, userId, nickname, gender, isAlarm, status, role,
                createdAtFrom, createdAtTo,
                birthDateFrom, birthDateTo,
                lastLoginFrom, lastLoginTo
        ).allMatch(field -> field == null);
    }
}
