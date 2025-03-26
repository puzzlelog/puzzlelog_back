package com.puzzlelog.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.config.CustomAccessDeniedException;
import com.puzzlelog.api.dao.document.UserHistory;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
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

    /**
     * 사용자 정보 수정 서비스
     * 사용자 본인의 정보(비밀번호, 닉네임, 생년월일, 성별, 알림 여부, 프로필 이미지 등)를 수정하며,
     * 관리자인 경우 상태(status)나 권한(role)도 수정 가능합니다.
     * 변경 이력은 UserHistory에 기록되며, 수정된 필드와 이전/이후 값도 함께 반환합니다.
     *
     * @param userId 수정할 사용자 ID (인증된 사용자 기준)
     * @param request 수정 요청 DTO
     * @param file 프로필 이미지 파일 (선택적)
     * @return 수정 결과 응답 DTO
     */
    @Transactional
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request, MultipartFile file) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, UserUpdateResponse.UpdateField> updatedFields = new HashMap<>();
        Map<String, Object> historyFields = new HashMap<>();

        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String editorId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        System.out.println("isAdmin : " + isAdmin);

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

                updatedFields.put("nickname",
                    new UserUpdateResponse.UpdateField(user.getNickname(), request.getNickname()));

                user.setNickname(request.getNickname());
            }

            if (request.hasBirthDate()) {
                String newBirthDateStr = request.getBirthDate();
                String prevBirthDateStr = user.getBirthDate() != null ? user.getBirthDate().toString() : null;

                if (!Objects.equals(newBirthDateStr, prevBirthDateStr)) {
                    Map<String, Object> birthDateHistory = new HashMap<>();
                    birthDateHistory.put("before", prevBirthDateStr);
                    birthDateHistory.put("after", newBirthDateStr);
                    historyFields.put("birthDate", birthDateHistory);
                    updatedFields.put("birthDate", new UserUpdateResponse.UpdateField(prevBirthDateStr, newBirthDateStr));
                    user.setBirthDate(newBirthDateStr != null ? LocalDate.parse(newBirthDateStr) : null);
                }
            }

            if (request.hasGender()) {
                String newGenderStr = request.getGender();
                String prevGenderStr = user.getGender();

                if (newGenderStr != null && !newGenderStr.equals("MALE") && !newGenderStr.equals("FEMALE")) {
                    throw new IllegalArgumentException("유효하지 않은 성별 값입니다: " + newGenderStr);
                }

                if (!Objects.equals(newGenderStr, prevGenderStr)) {
                    Map<String, Object> genderHistory = new HashMap<>();
                    genderHistory.put("before", prevGenderStr);
                    genderHistory.put("after", newGenderStr);
                    historyFields.put("gender", genderHistory);
                    updatedFields.put("gender", new UserUpdateResponse.UpdateField(prevGenderStr, newGenderStr));
                    user.setGender(newGenderStr);
                }
            }

            if (request.hasIsAlarm() && !request.getIsAlarm().equals(user.getIsAlarm())) {
                historyFields.put("isAlarm", Map.of("before", user.getIsAlarm(), "after", request.getIsAlarm()));
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

            /** 관리자 기능: 상태(status) 및 권한(role) 변경 **/ 
            // 일반 사용자가 다른 사용자 정보를 수정하려고 하면 차단
            if (!isAdmin && !editorId.equals(userId)) {
                throw new CustomAccessDeniedException("다른 사용자의 정보는 수정할 수 없습니다. 본인의 정보만 수정이 가능합니다.");
            }
            
            // 관리자 자신의 role/status 수정은 금지
            if (isAdmin && editorId.equals(userId)) {
                if (request.hasStatus()) {
                    throw new CustomAccessDeniedException("관리자는 자신의 상태(status)는 수정할 수 없습니다.");
                }
                if (request.hasRole()) {
                    throw new CustomAccessDeniedException("관리자는 자신의 권한(role)은 수정할 수 없습니다.");
                }
            }

            if (request.hasStatus()) {
                if (!isAdmin) {
                    throw new CustomAccessDeniedException("상태(status)는 관리자만 수정할 수 있습니다.");
                }

                String newStatusStr = request.getStatus();
                String prevStatusStr = user.getStatus();

                if (newStatusStr != null && !Set.of("ACTIVE", "BANNED").contains(newStatusStr)) {
                    throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + newStatusStr);
                }

                if (!Objects.equals(newStatusStr, prevStatusStr)) {
                    Map<String, Object> statusHistory = new HashMap<>();
                    statusHistory.put("before", prevStatusStr);
                    statusHistory.put("after", newStatusStr);
                    historyFields.put("status", statusHistory);
                    updatedFields.put("status", new UserUpdateResponse.UpdateField(prevStatusStr, newStatusStr));
                    user.setStatus(newStatusStr);
                }
            }

            if (request.hasRole()) {
                if (!isAdmin) {
                    throw new CustomAccessDeniedException("권한(role)은 관리자만 수정할 수 있습니다.");
                }

                String newRoleStr = request.getRole();
                String prevRoleStr = user.getRole();

                if (newRoleStr != null && !Set.of("USER", "ADMIN").contains(newRoleStr)) {
                    throw new IllegalArgumentException("유효하지 않은 권한 값입니다: " + newRoleStr);
                }

                if (!Objects.equals(newRoleStr, prevRoleStr)) {
                    Map<String, Object> roleHistory = new HashMap<>();
                    roleHistory.put("before", prevRoleStr);
                    roleHistory.put("after", newRoleStr);
                    historyFields.put("role", roleHistory);
                    updatedFields.put("role", new UserUpdateResponse.UpdateField(prevRoleStr, newRoleStr));
                    user.setRole(newRoleStr);
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

                historyFields.put("profileImg", Map.of("before", prevProfileImg, "after", mediaId));
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

        // 변경 이력 저장 - 관리자와 사용자 구분
        String reason = isAdmin && !editorId.equals(userId)
                ? request.getReason() // 관리자 수정 시 reason 필수
                : "본인 수정";

        if (isAdmin && !editorId.equals(userId) && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("관리자가 다른 사용자의 정보를 수정할 때는 사유(reason)를 입력해야 합니다.");
        }

        userHistoryRepository.save(UserHistory.builder()
            .userId(userId)
            .action("UPDATE")
            .reason(reason)
            .changedBy(editorId)
            .timestamp(LocalDateTime.now())
            .changedFields(historyFields)
            .build());

        return UserUpdateResponse.builder()
            .userId(user.getUserId())
            .updatedFields(updatedFields)
            .mediaId(mediaId)
            .reason(reason)
            .build();
    }
    
//    // 전체 사용자 조회 (페이징)
//    @Transactional(readOnly = true)
//    public Page<UserResponse> getAllUsers(int page, int size) {
//        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
//                .map(UserResponse::from);
//    }
//    
//    
//    @Transactional(readOnly = true)
//    public Page<UserResponse> findUsers(UserSearchRequest request, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        Specification<User> searchSpec = userListSearch.buildSearch(request);
//        Specification<User> jwtSpec = userListSearch.buildSearch(request);
//
//        Specification<User> combinedSpec = Specification.where(searchSpec).and(jwtSpec);
//
//        return userRepository.findAll(combinedSpec, pageable).map(UserResponse::from);
//    }
    

//    // 사용자 비활성화 (상태를 DELETED로 변경)
//    @Transactional
//    public void deactivateUser(String userId) {
//        User user = userRepository.findByUserId(userId)
//            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        Status prevStatus = user.getStatus();
//
//        user.setStatus(Status.DELETED);
//        userRepository.save(user);
//        
//        // ✅ MongoDB 삭제 기록 추가
//        userHistoryRepository.save(UserHistory.builder()
//            .userId(userId)
//            .action("DELETE")
//            .reason("본인 삭제")
//            .changedBy(userId)
//            .timestamp(LocalDateTime.now())
//            .changedFields(Map.of("status", Map.of("before", prevStatus != null ? prevStatus.name() : null, "after", Status.DELETED.name())))
//            .build());
//    }
// 
//    // 중복 체크
//    @Transactional(readOnly = true)
//    public boolean checkDuplicate(String type, String value) {
//        switch (type) {
//            case "userId":
//                return userRepository.existsByUserId(value);
//            case "email":
//                return userRepository.existsByEmail(value);
//            case "nickname":
//                return userRepository.existsByNickname(value);
//            default:
//                throw new IllegalArgumentException("잘못된 중복 체크 타입입니다: " + type);
//        }
//    }
}