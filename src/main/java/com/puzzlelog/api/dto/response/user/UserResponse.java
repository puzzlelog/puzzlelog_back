package com.puzzlelog.api.dto.response.user;

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
    private String nickname;
    private LocalDate birthDate;
    private String gender;
    private Boolean isAlarm;
    private String profileImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .isAlarm(user.getIsAlarm())
                .profileImg(user.getProfileImg())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }
}