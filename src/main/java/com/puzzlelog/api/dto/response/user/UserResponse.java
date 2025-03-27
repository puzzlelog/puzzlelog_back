package com.puzzlelog.api.dto.response.user;

import com.puzzlelog.api.dao.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 * 관리자 페이지 또는 사용자 정보 조회 시 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /** 사용자 DB 고유 번호 (PK) */
    private Integer id;

    /** 사용자 로그인 ID */
    private String userId;

    /** 닉네임 */
    private String nickname;

    /** 생년월일 */
    private LocalDate birthDate;

    /** 성별 (MALE, FEMALE) */
    private String gender;

    /** 알림 수신 여부 */
    private Boolean isAlarm;

    /** 프로필 이미지 URL */
    private String profileImg;

    /** 계정 생성일 */
    private LocalDateTime createdAt;

    /** 계정 마지막 수정일 */
    private LocalDateTime updatedAt;

    /** 사용자 상태 (ACTIVE, BANNED, DELETED) */
    private String status;

    /**
     * User 엔티티를 UserResponse DTO로 변환
     *
     * @param user User 엔티티
     * @return 변환된 DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .isAlarm(user.getIsAlarm())
                .profileImg(user.getProfileImg())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus())
                .build();
    }
}
