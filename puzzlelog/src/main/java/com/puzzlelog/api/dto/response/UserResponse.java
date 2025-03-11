package com.puzzlelog.api.dto.response;

import com.puzzlelog.api.dao.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String userId;
    private String email;
    private String nickname;
    private LocalDate birthDate;
    private User.Gender gender;
    private Boolean isAlarm;
    private String profileImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User.Status status;
    private LocalDateTime lastLogin;
    private User.Role role;

    // 엔티티에서 Response DTO로 변환
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .isAlarm(user.getIsAlarm())
                .profileImg(user.getProfileImg())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus())
                .lastLogin(user.getLastLogin())
                .role(user.getRole())
                .build();
    }
}
