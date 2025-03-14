package com.puzzlelog.api.dto.response.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String userId;
    private String token; // JWT 등 인증 토큰
}