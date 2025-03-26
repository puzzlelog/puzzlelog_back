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

    
    /**
     * 회원 가입 서비스
     * 사용자 요청 정보를 기반으로 회원 정보를 저장하고,
     * 선택적으로 프로필 이미지를 Cloudinary에 업로드합니다.
     *
     * @param request 회원가입 요청 정보 (아이디, 비밀번호, 이메일 등)
     * @param file 프로필 이미지 파일 (선택 사항)
     * @return 가입된 사용자 ID를 포함한 최소 응답 DTO
     */
    @Transactional
    public SignupResponse registerUser(SignupRequest request, MultipartFile file) {
        String profileImgUrl = null;

        // ✅ 1. 프로필 이미지 업로드 처리 (선택적)
        if (file != null && !file.isEmpty()) {
            try {
                String publicId = "$profile_" + request.getUserId();
                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadImageToCloud(file, publicId);
                profileImgUrl = uploadResult.getUrl();
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        // ✅ 2. 사용자 엔티티 생성
        User user = User.builder()
                .userId(request.getUserId())
                .userPwd(passwordEncoder.encode(request.getUserPwd()))
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .profileImg(profileImgUrl)
                .build();

        // ✅ 3. DB 저장
        userRepository.save(user);

        // ✅ 4. 최소 응답 반환 (userId만 포함)
        return SignupResponse.builder()
                .userId(user.getUserId())
                .build();
    }

    /**
     * 사용자 로그인 요청을 검증하고, 유효한 경우 User 엔티티를 반환합니다.
     *
     * @param request 로그인 요청 정보 (아이디, 비밀번호)
     * @return 로그인에 성공한 사용자 엔티티, 실패 시 null
     */
    public User validateUser(LoginRequest request) {
        return userRepository.findByUserId(request.getUserId())
                .filter(user -> passwordEncoder.matches(request.getUserPwd(), user.getUserPwd()))
                .orElse(null);
    }
}