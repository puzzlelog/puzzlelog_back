package com.puzzlelog.api.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.config.JwtProvider;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.auth.LoginRequest;
import com.puzzlelog.api.dto.request.auth.SignupRequest;
import com.puzzlelog.api.dto.response.auth.LoginResponse;
import com.puzzlelog.api.dto.response.auth.SignupResponse;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입 API
     */
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestPart("data") @Valid SignupRequest request,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // 💥 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("회원가입 유효성 실패: " + errorMessage));
        }

        // 💡 중복 아이디/이메일 검사
        if (authService.existsByUserId(request.getUserId())) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이미 존재하는 아이디입니다."));
        }

        if (authService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이미 존재하는 이메일입니다."));
        }

        SignupResponse response = authService.registerUser(request, file);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입 성공"));
    }


    /**
     * 로그인 API
     * 사용자의 로그인 요청을 처리하고, 성공 시 JWT 토큰을 발급하여 응답합니다.
     *
     * @param loginRequest 로그인 요청 정보 (userId, userPwd)
     * @return JWT 토큰이 포함된 응답 DTO
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("로그인 유효성 실패: " + errorMessage));
        }
        
        User user = authService.validateUser(loginRequest);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("아이디 또는 비밀번호가 잘못되었습니다."));
        }

        String token = jwtProvider.createToken(user.getId(), user.getUserId(), user.getRole());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));
    }
} 