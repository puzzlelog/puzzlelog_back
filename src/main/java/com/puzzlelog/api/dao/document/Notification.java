package com.puzzlelog.api.dao.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자가 받아보는 다양한 유형의 알림(Notification)을 저장하는 Document입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {

    /** 알림 Document의 고유 식별자 (MongoDB ObjectId) */
    @Id
    private String id;

    /** 알림을 받을 사용자의 ID (MySQL User 테이블의 userId 참조) */
    private String userId;

    /** 알림 메시지 내용 */
    private String message;

    /**
     * 알림의 유형
     * 가능한 값:
     * - "time_capsule": 타임캡슐 오픈 알림
     * - "memory_recommendation": 추억 추천 알림
     * - "interaction": 상호작용(댓글, 좋아요 등) 알림
     */
    private String type;

    /** 사용자가 알림을 읽었는지 여부 (기본값: false) */
    @Builder.Default
    private boolean isRead = false;

    /** 알림의 논리 삭제 여부 (기본값: false) */
    @Builder.Default
    private boolean deleted = false;

    /** 알림 생성 시각 (자동 저장, UTC 기준 ISO 8601 형식) */
    @CreatedDate
    private Instant createdAt;
}
