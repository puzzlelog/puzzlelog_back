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
    private String type;
    private String content;
    private List<String> tags;

    private Boolean privatePiece = false;  // 필드에서 기본값 지정 (가장 좋음)
    private Boolean deleted = false;       // 필드에서 기본값 지정 (가장 좋음)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;

    private Boolean today;

    public boolean hasNoCondition() {
        return userId == null &&
               type == null &&
               content == null &&
               (tags == null || tags.isEmpty()) &&
               createdAtFrom == null &&
               createdAtTo == null &&
               createdAt == null &&
               (today == null || !today);
    }

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
