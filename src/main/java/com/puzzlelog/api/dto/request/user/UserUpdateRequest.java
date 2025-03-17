package com.puzzlelog.api.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String userPwd;     // 비밀번호 (기존 비밀번호 확인 별도 로직 필요)
    private String nickname;    // 닉네임 (중복 체크)
    private String birthDate;   // 생년월일
    private String gender;      // 성별 (MALE/FEMALE)
    private Boolean isAlarm;    // 알림 설정 여부
    private String profileImg;  // 프로필 이미지 URL

    // 관리자 전용 필드
    private String status;      // ACTIVE, DELETED, BANNED
    private String role;        // USER, ADMIN
    
    public boolean isEmpty() {
        return userPwd == null &&
               nickname == null &&
               birthDate == null &&
               gender == null &&
               isAlarm == null &&
               profileImg == null &&
               status == null &&
               role == null;
    }
}
