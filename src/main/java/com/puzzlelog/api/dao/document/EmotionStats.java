package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * 특정 사용자의 특정 기간 동안의 감정 데이터를 분석하여 통계 정보를 제공하는 Document입니다.
 * 사용자의 감정 변화 추세 및 감정의 빈도를 분석하여 사용자에게 유용한 정보를 제공합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "emotion_stats")
public class EmotionStats {

    /** MongoDB Document 고유 식별자 */
    @Id
    private String id;

    /** 사용자 ID (MySQL의 users 테이블 userId 참조) */
    private String userId;

    /** 통계 기간의 시작일 (UTC 기준 ISO 8601 형식) */
    private Instant startDate;

    /** 통계 기간의 종료일 (UTC 기준 ISO 8601 형식) */
    private Instant endDate;

    /**
     * 기간 내 각 감정의 발생 횟수
     * 예시:
     * {
     *     "happy": 12,
     *     "sad": 5,
     *     "angry": 3,
     *     "neutral": 8
     * }
     */
    private Map<String, Integer> emotionCounts;

    /** 가장 자주 기록된 감정 (예: "happy") */
    private String mostFrequentEmotion;

    /** 증가 추세에 있는 감정 (예: "happy") */
    private String risingEmotion;

    /** 감소 추세에 있는 감정 (예: "sad") */
    private String decliningEmotion;

    /** 통계 데이터 생성 시각 (자동 저장, UTC 기준 ISO 8601 형식) */
    @CreatedDate
    private Instant createdAt;
}
