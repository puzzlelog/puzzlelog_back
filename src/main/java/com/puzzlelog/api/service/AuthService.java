package com.puzzlelog.api.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dao.entity.User.Role;
import com.puzzlelog.api.dto.request.auth.LoginRequest;
import com.puzzlelog.api.dto.request.auth.SignupRequest;
import com.puzzlelog.api.dto.response.auth.LoginResponse;
import com.puzzlelog.api.dto.response.auth.SignupResponse;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }
    
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public SignupResponse registerUser(SignupRequest request, MultipartFile file) {
        String profileImgUrl = null;

        if (file != null && !file.isEmpty()) {
            try {
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

    public LoginResponse validateUser(LoginRequest request) {
        return userRepository.findByUserId(request.getUserId())
                .filter(user -> passwordEncoder.matches(request.getUserPwd(), user.getUserPwd()))
                .map(user -> LoginResponse.builder()
                        .id(user.getId())
                        .userId(user.getUserId())
                        .role(user.getRole().name())
                        .token(null)
                        .build())
                .orElse(null);
    }
    
    public boolean isAdmin(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }
}