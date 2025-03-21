package com.puzzlelog.api.dto.request.piece;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceSearchRequest {
    private String userId;
    private String type;  // TEXT, IMAGE, VIDEO, AUDIO
    private String content;          // LIKE 검색을 위한 키워드
    private List<String> tags;       // 태그 검색 (태그 중 하나라도 포함)

    private Boolean isPrivate;
    private Boolean isDeleted;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;

    // 추가된 필드
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;  // 특정 날짜 조각 조회용

    private Boolean today;  // 오늘 날짜 조회 여부

    // 편의 메서드: 조건이 없으면 true 반환
    public boolean hasNoCondition() {
        return userId == null &&
               type == null &&
               content == null &&
               (tags == null || tags.isEmpty()) &&
               isPrivate == null &&
               isDeleted == null &&
               createdAtFrom == null &&
               createdAtTo == null &&
               createdAt == null &&
               (today == null || !today);
    }

    // 오늘 날짜로 필터링 (편의 메서드)
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
}
