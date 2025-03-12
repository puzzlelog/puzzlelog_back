package com.puzzlelog.api.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.LoginRequest;
import com.puzzlelog.api.dto.request.SignupRequest;
import com.puzzlelog.api.dto.response.LoginResponse;
import com.puzzlelog.api.dto.response.SignupResponse;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class AuthService { // 인증 기능

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // 아이디 중복 체크
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 이메일 중복 체크
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원가입 로직
    @Transactional
    public SignupResponse registerUser(SignupRequest request) {
        User savedUser = userRepository.save(User.builder()
                .userId(request.getUserId())
                .userPwd(passwordEncoder.encode(request.getUserPwd()))
                .email(request.getEmail())
                .birthDate(request.getBirthDate() != null ?
                        LocalDate.parse(request.getBirthDate()) : null)
                .gender(request.getGender() != null ?
                        User.Gender.valueOf(request.getGender()) : null)
                .build());

        return SignupResponse.builder()
                .id(savedUser.getId())
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .build();
    }

    // 로그인 로직 (명확한 DTO 반환)
    public LoginResponse validateUser(LoginRequest request) {
        return userRepository.findByUserId(request.getUserId())
                .filter(user -> passwordEncoder.matches(request.getUserPwd(), user.getUserPwd()))
                .map(user -> LoginResponse.builder()
                        .userId(user.getUserId())
                        .token(null) // JWT 적용 전 null
                        .build())
                .orElse(null);
    }
}
