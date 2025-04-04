package com.puzzlelog.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.document.UserHistory;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dao.entity.User.Gender;
import com.puzzlelog.api.dao.entity.User.Status;
import com.puzzlelog.api.dao.entity.User.Role;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.repository.listsearch.UserListSearch;
import com.puzzlelog.api.repository.mongo.UserHistoryRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    
    private final UserListSearch userListSearch;
    private final UserHistoryRepository userHistoryRepository;

    // 전체 사용자 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(UserResponse::from);
    }
    
    
    @Transactional(readOnly = true)
    public Page<UserResponse> findUsers(UserSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<User> searchSpec = userListSearch.buildSearch(request);
        Specification<User> jwtSpec = userListSearch.buildSearch(request);

        Specification<User> combinedSpec = Specification.where(searchSpec).and(jwtSpec);

        return userRepository.findAll(combinedSpec, pageable).map(UserResponse::from);
    }
    

    // 사용자 정보 수정
    @Transactional
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request, MultipartFile file) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, UserUpdateResponse.UpdateField> updatedFields = new HashMap<>();
        Map<String, Object> historyFields = new HashMap<>();

        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }

        if (request != null) {
            if (request.hasUserPwd()) {
                user.setUserPwd(passwordEncoder.encode(request.getUserPwd()));
                updatedFields.put("userPwd", new UserUpdateResponse.UpdateField("(비밀번호 변경됨)", "(비밀번호 변경됨)"));
                historyFields.put("userPwd", "(비밀번호 변경됨)");
            }

            if (request.hasNickname() && !Objects.equals(request.getNickname(), user.getNickname())) {
                Map<String, Object> nicknameHistory = new HashMap<>();
                nicknameHistory.put("before", user.getNickname());
                nicknameHistory.put("after", request.getNickname());

                historyFields.put("nickname", nicknameHistory);
                updatedFields.put("nickname", new UserUpdateResponse.UpdateField(user.getNickname(), request.getNickname()));
                user.setNickname(request.getNickname());
            }

            if (request.hasBirthDate()) {
                String prevBirthDate = user.getBirthDate() != null ? user.getBirthDate().toString() : null;

                if (!Objects.equals(request.getBirthDate(), prevBirthDate)) {
                    Map<String, Object> birthDateHistory = new HashMap<>();
                    birthDateHistory.put("before", prevBirthDate);
                    birthDateHistory.put("after", request.getBirthDate());

                    historyFields.put("birthDate", birthDateHistory);
                    updatedFields.put("birthDate", new UserUpdateResponse.UpdateField(prevBirthDate, request.getBirthDate()));

                    user.setBirthDate(request.getBirthDate() != null ? LocalDate.parse(request.getBirthDate()) : null);
                }
            }
            
            if (request.hasGender()) {
                Gender prevGender = user.getGender();
                String prevGenderStr = prevGender != null ? prevGender.name() : null;
                String newGenderStr = request.getGender();

                if (!Objects.equals(newGenderStr, prevGenderStr)) {
                    Map<String, Object> genderHistory = new HashMap<>();
                    genderHistory.put("before", prevGenderStr);
                    genderHistory.put("after", newGenderStr);

                    historyFields.put("gender", genderHistory);
                    updatedFields.put("gender", new UserUpdateResponse.UpdateField(prevGenderStr, newGenderStr));

                    user.setGender(newGenderStr != null ? Gender.valueOf(newGenderStr) : null);
                }
            }

            if (request.hasIsAlarm() && !request.getIsAlarm().equals(user.getIsAlarm())) {
                Map<String, Object> isAlarmHistory = new HashMap<>();
                isAlarmHistory.put("before", user.getIsAlarm());
                isAlarmHistory.put("after", request.getIsAlarm());

                historyFields.put("isAlarm", isAlarmHistory);
                updatedFields.put("isAlarm", new UserUpdateResponse.UpdateField(user.getIsAlarm(), request.getIsAlarm()));
                user.setIsAlarm(request.getIsAlarm());
            }

            if (request.hasProfileImg() && request.getProfileImg() == null && user.getProfileImg() != null && (file == null || file.isEmpty())) {
                String publicId = "$profile_" + userId;
                String prevProfileImg = user.getProfileImg();

                try {
                    boolean deleted = cloudinaryService.deleteFromCloud(publicId, "image");
                    if (!deleted) {
                        logger.warn("프로필 이미지가 이미 삭제되었거나 존재하지 않습니다: {}", publicId);
                    }

                    Map<String, Object> profileImgHistory = new HashMap<>();
                    profileImgHistory.put("before", prevProfileImg);
                    profileImgHistory.put("after", null);

                    historyFields.put("profileImg", profileImgHistory);
                    updatedFields.put("profileImg", new UserUpdateResponse.UpdateField(prevProfileImg, null));

                    user.setProfileImg(null);
                } catch (Exception e) {
                    throw new RuntimeException("프로필 이미지 삭제 실패: " + e.getMessage(), e);
                }
            }
            
            /**
             * @Admin : 관리자 기능
             */
            
            if (request.hasStatus()) {
                Status prevStatus = user.getStatus();
                String prevStatusStr = prevStatus != null ? prevStatus.name() : null;
                String newStatusStr = request.getStatus();

                if (!Objects.equals(newStatusStr, prevStatusStr)) {
                    Map<String, Object> statusHistory = new HashMap<>();
                    statusHistory.put("before", prevStatusStr);
                    statusHistory.put("after", newStatusStr);

                    historyFields.put("status", statusHistory);
                    updatedFields.put("status", new UserUpdateResponse.UpdateField(prevStatusStr, newStatusStr));
                    user.setStatus(newStatusStr != null ? Status.valueOf(newStatusStr) : null);
                }
            }

            if (request.hasRole()) {
                Role prevRole = user.getRole();
                String prevRoleStr = prevRole != null ? prevRole.name() : null;
                String newRoleStr = request.getRole();

                if (!Objects.equals(newRoleStr, prevRoleStr)) {
                    Map<String, Object> roleHistory = new HashMap<>();
                    roleHistory.put("before", prevRoleStr);
                    roleHistory.put("after", newRoleStr);

                    historyFields.put("role", roleHistory);
                    updatedFields.put("role", new UserUpdateResponse.UpdateField(prevRoleStr, newRoleStr));
                    user.setRole(newRoleStr != null ? Role.valueOf(newRoleStr) : null);
                }
            }
        }

        String mediaId = null;
        if (file != null && !file.isEmpty()) {
            String publicId = "$profile_" + userId;
            String prevProfileImg = user.getProfileImg();
            try {
                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadImageToCloud(file, publicId);
                mediaId = uploadResult.getUrl();

                Map<String, Object> profileImgHistory = new HashMap<>();
                profileImgHistory.put("before", prevProfileImg);
                profileImgHistory.put("after", mediaId);

                historyFields.put("profileImg", profileImgHistory);
                updatedFields.put("profileImg", new UserUpdateResponse.UpdateField(prevProfileImg, mediaId));

                user.setProfileImg(mediaId);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        if (updatedFields.isEmpty()) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }

        userRepository.save(user);

        userHistoryRepository.save(UserHistory.builder()
            .userId(userId)
            .action("UPDATE")
            .reason("본인 수정")
            .changedBy(userId)
            .timestamp(LocalDateTime.now())
            .changedFields(historyFields)
            .build());

        return UserUpdateResponse.builder()
            .userId(user.getUserId())
            .updatedFields(updatedFields)
            .mediaId(mediaId)
            .build();
    }

    // 사용자 비활성화 (상태를 DELETED로 변경)
    @Transactional
    public void deactivateUser(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Status prevStatus = user.getStatus();

        user.setStatus(Status.DELETED);
        userRepository.save(user);
        
        // ✅ MongoDB 삭제 기록 추가
        userHistoryRepository.save(UserHistory.builder()
            .userId(userId)
            .action("DELETE")
            .reason("본인 삭제")
            .changedBy(userId)
            .timestamp(LocalDateTime.now())
            .changedFields(Map.of("status", Map.of("before", prevStatus != null ? prevStatus.name() : null, "after", Status.DELETED.name())))
            .build());
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