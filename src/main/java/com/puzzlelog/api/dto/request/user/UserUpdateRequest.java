package com.puzzlelog.api.dto.request.user;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 수정 요청 DTO
 * 프론트엔드에서 전달받은 사용자 수정 필드를 담으며,
 * 어떤 필드가 실제로 수정되었는지를 추적하기 위해 내부적으로 필드 이름을 저장합니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {

    /** 사용자 비밀번호 (선택) */
    private String userPwd;

    /** 사용자 닉네임 (선택) */
    private String nickname;

    /** 생년월일 (형식: YYYY-MM-DD, 선택) */
    private String birthDate;

    /** 성별 ("MALE", "FEMALE", 선택) */
    private String gender;

    /** 알림 수신 여부 (선택) */
    private Boolean isAlarm;

    /** 프로필 이미지 URL (선택) */
    private String profileImg;

    /** 사용자 상태 (예: "ACTIVE", "DELETED", 선택) */
    private String status;

    /** 사용자 권한 (예: "USER", "ADMIN", 선택) */
    private String role;

    /** 관리자 수정 시 사유 (선택, 관리자용) */
    private String reason;

    /** 어떤 필드가 수정되었는지 추적하는 내부 Set */
    @JsonIgnore
    private final Set<String> fieldsSet = new HashSet<>();

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

    public boolean hasUserPwd() { return fieldsSet.contains("userPwd"); }
    public boolean hasNickname() { return fieldsSet.contains("nickname"); }
    public boolean hasBirthDate() { return fieldsSet.contains("birthDate"); }
    public boolean hasGender() { return fieldsSet.contains("gender"); }
    public boolean hasIsAlarm() { return fieldsSet.contains("isAlarm"); }
    public boolean hasProfileImg() { return fieldsSet.contains("profileImg"); }
    public boolean hasStatus() { return fieldsSet.contains("status"); }
    public boolean hasRole() { return fieldsSet.contains("role"); }

    /**
     * 전달된 필드가 아무것도 없는 경우 true 반환
     */
    public boolean isEmpty() {
        return fieldsSet.isEmpty();
    }
}
