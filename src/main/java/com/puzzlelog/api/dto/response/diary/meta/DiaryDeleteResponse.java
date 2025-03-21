package com.puzzlelog.api.dto.response.diary.meta;

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
public class DiaryDeleteResponse {
    private String diaryId; // 삭제 처리된 일기 ID
    private String userId;  // 사용자 ID

    public static DiaryDeleteResponse of(String diaryId, String userId) {
        return DiaryDeleteResponse.builder()
            .diaryId(diaryId)
            .userId(userId)
            .build();
    }
}
