package com.puzzlelog.api.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, UserUpdateResponse.UpdateField> updatedFields = new HashMap<>();

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
        if (request.getProfileImg() != null && !request.getProfileImg().equals(user.getProfileImg())) {
            updatedFields.put("profileImg", new UserUpdateResponse.UpdateField(user.getProfileImg(), request.getProfileImg()));
            user.setProfileImg(request.getProfileImg());
        }

        // 관리자 전용 필드
        if (request.getStatus() != null && !request.getStatus().equals(user.getStatus().name())
            && !request.getStatus().equals(User.Status.DELETED.name())) {
            updatedFields.put("status", new UserUpdateResponse.UpdateField(user.getStatus().name(), request.getStatus()));
            user.setStatus(User.Status.valueOf(request.getStatus()));
        }
        if (request.getRole() != null && !request.getRole().equals(user.getRole().name())) {
            updatedFields.put("role", new UserUpdateResponse.UpdateField(user.getRole().name(), request.getRole()));
            user.setRole(User.Role.valueOf(request.getRole()));
        }

        userRepository.save(user);

        return UserUpdateResponse.builder()
            .userId(user.getUserId())
            .updatedFields(updatedFields)
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
