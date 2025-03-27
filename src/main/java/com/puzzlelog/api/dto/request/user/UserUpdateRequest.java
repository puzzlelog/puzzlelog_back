package com.puzzlelog.api.dto.request.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해야 합니다.")
    private String userPwd;

    @Size(max = 50, message = "닉네임은 최대 50자까지 입력할 수 있습니다.")
    private String nickname;

    /** 생년월일은 문자열로 받지만, 파싱은 서비스 단에서 진행 */
    private String birthDate;

    @Pattern(regexp = "MALE|FEMALE", message = "성별은 MALE 또는 FEMALE 중 하나여야 합니다.")
    private String gender;

    private Boolean isAlarm;

    private String profileImg;

    @Pattern(regexp = "ACTIVE|DELETED|BANNED", message = "상태는 ACTIVE, DELETED, BANNED 중 하나여야 합니다.")
    private String status;

    @Pattern(regexp = "USER|ADMIN", message = "권한은 USER 또는 ADMIN 이어야 합니다.")
    private String role;

    @Size(max = 255, message = "사유는 최대 255자까지 입력할 수 있습니다.")
    private String reason;

    @Size(max = 255, message = "차단 사유는 최대 255자까지 입력할 수 있습니다.")
    private String banReason;

    private String rawBanUntil;

    @JsonIgnore
    private final Set<String> fieldsSet = new HashSet<>();

    // --- 필드별 Setter는 생략 없이 유지 ---
    @JsonSetter("userPwd")
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
        fieldsSet.add("userPwd");
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
        fieldsSet.add("nickname");
    }

    @JsonSetter("birthDate")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        fieldsSet.add("birthDate");
    }

    @JsonSetter("gender")
    public void setGender(String gender) {
        this.gender = gender;
        fieldsSet.add("gender");
    }

    @JsonSetter("isAlarm")
    public void setIsAlarm(Boolean isAlarm) {
        this.isAlarm = isAlarm;
        fieldsSet.add("isAlarm");
    }

    @JsonSetter("profileImg")
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
        fieldsSet.add("profileImg");
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
        fieldsSet.add("status");
    }

    @JsonSetter("role")
    public void setRole(String role) {
        this.role = role;
        fieldsSet.add("role");
    }

    @JsonSetter("reason")
    public void setReason(String reason) {
        this.reason = reason;
        fieldsSet.add("reason");
    }

    @JsonSetter("banReason")
    public void setBanReason(String banReason) {
        this.banReason = banReason;
        fieldsSet.add("banReason");
    }

    @JsonSetter("banUntil")
    public void setRawBanUntil(String rawBanUntil) {
        this.rawBanUntil = rawBanUntil;
        fieldsSet.add("banUntil");
    }

    public boolean hasUserPwd() { return fieldsSet.contains("userPwd"); }
    public boolean hasNickname() { return fieldsSet.contains("nickname"); }
    public boolean hasBirthDate() { return fieldsSet.contains("birthDate"); }
    public boolean hasGender() { return fieldsSet.contains("gender"); }
    public boolean hasIsAlarm() { return fieldsSet.contains("isAlarm"); }
    public boolean hasProfileImg() { return fieldsSet.contains("profileImg"); }
    public boolean hasStatus() { return fieldsSet.contains("status"); }
    public boolean hasRole() { return fieldsSet.contains("role"); }
    public boolean hasReason() { return fieldsSet.contains("reason"); }
    public boolean hasBanReason() { return fieldsSet.contains("banReason"); }
    public boolean hasBanUntil() { return fieldsSet.contains("banUntil"); }

    public boolean isEmpty() {
        return fieldsSet.isEmpty();
    }

    @JsonIgnore
    public LocalDateTime getBanUntil() {
        if (rawBanUntil == null || rawBanUntil.isBlank()) return null;
        try {
            return rawBanUntil.contains("T")
                    ? LocalDateTime.parse(rawBanUntil)
                    : LocalDate.parse(rawBanUntil).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("banUntil 형식이 잘못되었습니다. yyyy-MM-dd 또는 yyyy-MM-dd'T'HH:mm:ss 형식을 사용하세요.");
        }
    }
}
