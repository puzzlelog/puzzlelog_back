package com.puzzlelog.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    private String userId;      // 필수
    private String userPwd;     // 필수 (일반 회원가입인 경우)
    private String email;       // 필수
    private String birthDate;   // 선택 (nullable)
    private String gender;      // 선택 (nullable)
    private String profileImg;  // 선택 (null일 경우 이미지 없음, "uploading"일 경우 이미지 업로딩 중)
}