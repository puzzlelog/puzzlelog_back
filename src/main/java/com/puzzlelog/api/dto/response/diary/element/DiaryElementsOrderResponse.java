package com.puzzlelog.api.dto.response.diary.element;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryElementsOrderResponse {
    private String diaryId;
    private List<String> elementIds;
    private Instant updatedAt;

    public static DiaryElementsOrderResponse of(String diaryId, List<String> elementIds, Instant updatedAt) {
        return DiaryElementsOrderResponse.builder()
                .diaryId(diaryId)
                .elementIds(elementIds)
                .updatedAt(updatedAt)
                .build();
    }
}