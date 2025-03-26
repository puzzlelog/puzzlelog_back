package com.puzzlelog.api.dto.request.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 * 사용자가 로그인 시 입력하는 사용자 식별자와 비밀번호를 담는 요청 객체입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /**
     * 사용자 고유 아이디 (로그인 ID)
     */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String userId;

    /**
     * 사용자 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String userPwd;
} 
