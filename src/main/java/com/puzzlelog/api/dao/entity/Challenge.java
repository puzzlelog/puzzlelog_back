package com.puzzlelog.api.dao.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 챌린지 정보 관리를 위한 Entity (MySQL 테이블: challenges)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "challenges",
       indexes = {
           @Index(name = "idx_challenge_type", columnList = "type"),
           @Index(name = "idx_is_active", columnList = "is_active")
       })
public class Challenge {

    /** 챌린지의 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 챌린지 제목 */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /** 챌린지 상세 설명 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 챌린지 유형 (attendance, mission, event, quiz 등 문자열로 관리) */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 챌린지 지속 기간 (일 단위) */
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    /** 챌린지 완료 시 지급할 포인트 */
    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;

    /** 챌린지 활성화 여부 (true: 활성화, false: 비활성화) */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /** 챌린지 생성 일시 (자동 설정) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 챌린지 수정 일시 (자동 갱신) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 생성일 자동 설정 */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    /** 수정일 자동 갱신 */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
