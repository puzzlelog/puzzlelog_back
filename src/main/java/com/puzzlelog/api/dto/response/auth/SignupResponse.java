package com.puzzlelog.api.dto.response.auth;

import lombok.*;

/**
 * 회원가입 성공 시 클라이언트에 전달되는 최소 정보 응답 DTO
 * 가입된 사용자의 식별 가능한 ID만 포함하여 보안성과 단순성을 유지합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {

    /**
     * 사용자가 설정한 고유 아이디 (로그인용)
     */
    private String userId;
} 
