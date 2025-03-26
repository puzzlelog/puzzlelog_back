package com.puzzlelog.api.dao.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

/**
 * 사용자가 특정 날짜에 기록한 감정(Emotion)을 나타내는 MongoDB Document입니다.
 * 일기(Diary)와 연결될 수 있으며, 캘린더에서 사용자의 감정을 시각적으로 표시할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "emotions")
public class Emotion {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 사용자 ID (MySQL의 users 테이블 userId 참조) */
    private String userId;

    /** 감정이 기록된 날짜 (UTC 기준 ISO 8601 형식, 시간은 00:00:00) */
    private Instant date;

    /**
     * 사용자의 감정 상태
     * 가능한 값:
     * - "happy": 행복함
     * - "sad": 슬픔
     * - "angry": 분노
     * - "neutral": 중립
     * - "excited": 신남
     * - "anxious": 불안
     */
    private String emotion;

    /** 감정을 표현하는 이모지 (예: 😊, 😢, 😡 등) */
    private String emoji;

    /** 해당 감정과 연결된 일기(Diary)의 MongoDB ObjectId */
    private String diaryId;

    /** 감정 상태를 표현하는 대표 색상 (예: 기쁨 - #FFD700, 슬픔 - #0000FF 등) */
    private String color;

    /** 감정 데이터가 생성된 시각 (자동 저장, UTC 기준 ISO 8601 형식) */
    @CreatedDate
    private Instant createdAt;
}
