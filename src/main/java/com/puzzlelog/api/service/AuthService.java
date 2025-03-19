package com.puzzlelog.api.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.auth.LoginRequest;
import com.puzzlelog.api.dto.request.auth.SignupRequest;
import com.puzzlelog.api.dto.response.auth.LoginResponse;
import com.puzzlelog.api.dto.response.auth.SignupResponse;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class AuthService { // 인증 기능

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
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
    public SignupResponse registerUser(SignupRequest request, MultipartFile file) {
        String profileImgUrl = null;

        if (file != null && !file.isEmpty()) {
            try {
                // 프로필 이미지는 고유한 public_id로 업로드
                String publicId = "$profile_" + request.getUserId();

                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadImageToCloud(file, publicId);

                profileImgUrl = uploadResult.getUrl();

            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        User user = User.builder()
                .userId(request.getUserId())
                .userPwd(passwordEncoder.encode(request.getUserPwd()))
                .email(request.getEmail())
                .birthDate(request.getBirthDate() != null ? LocalDate.parse(request.getBirthDate()) : null)
                .gender(request.getGender() != null ? User.Gender.valueOf(request.getGender()) : null)
                .profileImg(profileImgUrl)
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.builder()
                .id(savedUser.getId())
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .profileImg(savedUser.getProfileImg())
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
    
    // 관리자 확인
    public boolean isAdmin(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> user.getRole() == User.Role.ADMIN)
                .orElse(false);
    }
}
