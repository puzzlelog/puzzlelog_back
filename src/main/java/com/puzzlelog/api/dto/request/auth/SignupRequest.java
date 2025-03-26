package com.puzzlelog.api.dto.request.auth;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * 회원가입 요청 DTO
 * 사용자가 회원가입 시 전달하는 정보를 담고 있으며,
 * 유효성 검증 어노테이션을 통해 사전 입력 검사를 수행합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    /**
     * 사용자 고유 아이디 (필수, 중복 불가)
     * - 공백 불가
     * - 최대 50자 제한
     */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(max = 50, message = "아이디는 최대 50자까지 가능합니다.")
    private String userId;

    /**
     * 사용자 비밀번호 (필수)
     * - 공백 불가
     * - 8~100자 사이 길이 제한
     * - 실제 저장 시 암호화 처리 필요
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8~100자 이내여야 합니다.")
    private String userPwd;

    /**
     * 사용자 이메일 주소 (필수)
     * - 공백 불가
     * - 이메일 형식 검증
     * - 최대 100자 제한
     * - 중복 불가
     */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 최대 100자까지 가능합니다.")
    private String email;

    /**
     * 생년월일 (선택)
     * - 형식: YYYY-MM-DD
     * - 클라이언트에서 문자열로 전달되어 LocalDate로 변환됨
     */
    private LocalDate birthDate;

    /**
     * 사용자 성별 (선택)
     * - 허용 값: "MALE", "FEMALE"
     * - 빈 문자열 또는 null 허용
     */
    @Pattern(regexp = "^(MALE|FEMALE)?$", message = "성별은 MALE 또는 FEMALE이어야 합니다.")
    private String gender;
} 
