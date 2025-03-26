package com.puzzlelog.api.dao.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * 사용자의 포인트 적립 및 사용 이력을 기록하는 MongoDB Document입니다.
 * 각 포인트 항목은 획득 또는 차감된 포인트의 상세 내역을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "points")
public class Point {

    /** MongoDB Document 고유 식별자 */
    @Id
    private String id;

    /** 사용자 ID (MySQL의 users 테이블의 id 참조) */
    private Integer userId;

    /**
     * 포인트 유형
     * 예시: "challenge", "attendance", "purchase", "event" 등
     */
    private String type;

    /** 포인트의 증감량 (양수: 획득, 음수: 차감) */
    private Integer amount;

    /** 포인트 적립/차감 상세 설명 */
    private String description;

    /**
     * 관련 챌린지 ID (MySQL challenges 테이블의 id 참조)
     * 챌린지와 관련 없을 경우 null
     */
    private Integer relatedChallengeId;

    /** 포인트 이력 생성일시 (UTC 기준) */
    @CreatedDate
    private Instant createdAt;
}
