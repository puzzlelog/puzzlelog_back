package com.puzzlelog.api.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public UserService(UserRepository userRepository, 
            PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.cloudinaryService = cloudinaryService;
	}

    // 전체 사용자 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(UserResponse::from);
    }

    // 특정 사용자 정보 조회
    @Transactional(readOnly = true)
    public Page<UserResponse> findUsers(UserSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        return userRepository.searchUsers(request, pageable)
                .map(UserResponse::from);
    }
    
    // 사용자 정보 수정
    @Transactional
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request, MultipartFile file) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, UserUpdateResponse.UpdateField> updatedFields = new HashMap<>();

        if (request != null) {
            if (request.getUserPwd() != null) {
                user.setUserPwd(passwordEncoder.encode(request.getUserPwd()));
                updatedFields.put("userPwd", new UserUpdateResponse.UpdateField("(비밀번호 변경됨)", "(비밀번호 변경됨)"));
            }

            if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
                updatedFields.put("nickname", new UserUpdateResponse.UpdateField(user.getNickname(), request.getNickname()));
                user.setNickname(request.getNickname());
            }

            if (request.getBirthDate() != null && !request.getBirthDate().equals(user.getBirthDate().toString())) {
                updatedFields.put("birthDate", new UserUpdateResponse.UpdateField(user.getBirthDate().toString(), request.getBirthDate()));
                user.setBirthDate(LocalDate.parse(request.getBirthDate()));
            }

            if (request.getGender() != null && !request.getGender().equals(user.getGender().name())) {
                updatedFields.put("gender", new UserUpdateResponse.UpdateField(user.getGender().name(), request.getGender()));
                user.setGender(User.Gender.valueOf(request.getGender()));
            }

            if (request.getIsAlarm() != null && !request.getIsAlarm().equals(user.getIsAlarm())) {
                updatedFields.put("isAlarm", new UserUpdateResponse.UpdateField(user.getIsAlarm(), request.getIsAlarm()));
                user.setIsAlarm(request.getIsAlarm());
            }

            // 프로필 이미지 삭제 처리 (null로 설정 시)
            if (request.getProfileImg() == null && user.getProfileImg() != null && (file == null || file.isEmpty())) {
                String publicId = "$profile_" + userId;
                try {
                    boolean deleted = cloudinaryService.deleteFromCloud(publicId, "image");
                    if (!deleted) {
                        logger.warn("프로필 이미지가 이미 삭제되었거나 존재하지 않습니다: {}", publicId);
                    }
                    updatedFields.put("profileImg", new UserUpdateResponse.UpdateField(user.getProfileImg(), null));
                    user.setProfileImg(null);
                } catch (Exception e) {
                    throw new RuntimeException("프로필 이미지 삭제 실패: " + e.getMessage(), e);
                }
            }
        }

        // 프로필 이미지 업로드 처리 (덮어쓰기이므로 이전/이후 구분 없이 바로 mediaId 반환)
        String mediaId = null;
        if (file != null && !file.isEmpty()) {
            String publicId = "$profile_" + userId;
            try {
                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadImageToCloud(file, publicId);
                mediaId = uploadResult.getUrl(); // 업로드된 URL
                updatedFields.put("profileImg", new UserUpdateResponse.UpdateField(user.getProfileImg(), mediaId));
                user.setProfileImg(mediaId);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        if (updatedFields.isEmpty()) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }

        userRepository.save(user);

        return UserUpdateResponse.builder()
            .userId(user.getUserId())
            .updatedFields(updatedFields)
            .mediaId(mediaId)  // 새로 추가된 필드, 프로필 이미지 URL 직접 반환
            .build();
    }  

    // 사용자 비활성화 (상태를 DELETED로 변경)
    @Transactional
    public void deactivateUser(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setStatus(User.Status.DELETED);
        userRepository.save(user);
    }
    
    // 중복 체크
    @Transactional(readOnly = true)
    public boolean checkDuplicate(String type, String value) {
        switch (type) {
            case "userId":
                return userRepository.existsByUserId(value);
            case "email":
                return userRepository.existsByEmail(value);
            case "nickname":
                return userRepository.existsByNickname(value);
            default:
                throw new IllegalArgumentException("잘못된 중복 체크 타입입니다: " + type);
        }
    }
}
