package com.puzzlelog.api.dto.response.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private Integer id;
    private String userId;
    private String email;
    private String profileImg; // 프로필 이미지 상태 ("uploading", URL 또는 null)
}