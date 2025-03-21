package com.puzzlelog.api.dto.response.diary.element;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryElementDeleteResponse {
    private String diaryId;   // 일기 ID
    private String elementId; // 삭제 처리된 요소 ID

    public static DiaryElementDeleteResponse of(String diaryId, String elementId) {
        return DiaryElementDeleteResponse.builder()
            .diaryId(diaryId)
            .elementId(elementId)
            .build();
    }
}
