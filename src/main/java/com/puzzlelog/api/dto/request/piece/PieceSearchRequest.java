package com.puzzlelog.api.dto.request.piece;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.*;

/**
 * 조각 목록 검색 요청 DTO입니다.
 * 다양한 필터 조건을 기반으로 MongoDB에서 조각을 조회할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceSearchRequest {

    /** 조각 작성자의 사용자 ID (명시하지 않으면 조건 없음) */
    private String userId;

    /** 조각 타입 (TEXT, IMAGE, VIDEO, AUDIO 중 하나) */
    private String type;

    /** 내용 검색 (텍스트 조각의 내용 일부 검색) */
    private String content;

    /** 검색할 태그 목록 */
    private List<String> tags;

    /** 비공개 여부 필터링 (명시된 경우에만 적용됨) */
    private Boolean privatePiece;

    /** 삭제 여부 필터링 (명시된 경우에만 적용됨) */
    private Boolean deleted;

    /** 조회 시작일 (yyyy-MM-dd 형식) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;

    /** 조회 종료일 (yyyy-MM-dd 형식) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;

    /** 특정 하루만 조회하고 싶을 때 사용 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;

    /** 오늘 날짜 기준으로 조회할지 여부 */
    private Boolean today;

    /**
     * 필터 조건이 전혀 없는 경우 true를 반환합니다.
     * 기본 조회인지 여부를 판단할 때 사용됩니다.
     */
    public boolean hasNoCondition() {
        return isNullOrEmpty(userId) &&
               isNullOrEmpty(type) &&
               isNullOrEmpty(content) &&
               (tags == null || tags.isEmpty()) &&
               createdAtFrom == null &&
               createdAtTo == null &&
               createdAt == null &&
               (today == null || !today);
    }

    /**
     * createdAt / today 필드를 createdAtFrom/To로 자동 변환합니다.
     * 날짜 필터를 한 범위로 통합해서 조회할 수 있도록 합니다.
     */
    public void applyDateFilters() {
        if (Boolean.TRUE.equals(today)) {
            LocalDate now = LocalDate.now();
            createdAtFrom = now;
            createdAtTo = now;
        } else if (createdAt != null) {
            createdAtFrom = createdAt;
            createdAtTo = createdAt;
        }
    }

    /** 내부 유틸: 문자열 null 또는 빈 문자열인지 검사 */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
