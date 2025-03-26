package com.puzzlelog.api.dto.response.auth;

import lombok.*;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO
 * 최소한의 정보로 JWT 토큰만 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * JWT 액세스 토큰
     */
    private String token;
} 