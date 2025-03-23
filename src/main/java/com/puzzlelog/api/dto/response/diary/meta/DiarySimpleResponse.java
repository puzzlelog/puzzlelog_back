package com.puzzlelog.api.dto.response.diary.meta;

import java.time.Instant;

import com.puzzlelog.api.dao.document.Diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiarySimpleResponse {
    private String diaryId;
    private String userId;
    private String title;
    private String backgroundContentId;
    private String themeColor;
    private String emotionContentId;
    private Boolean shared;
    private Instant openAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static DiarySimpleResponse from(Diary diary) {
        return DiarySimpleResponse.builder()
            .diaryId(diary.getId())
            .userId(diary.getUserId())
            .title(diary.getTitle())
            .backgroundContentId(diary.getBackgroundContentId())
            .themeColor(diary.getThemeColor())
            .emotionContentId(diary.getEmotionContentId())
            .shared(diary.isShared())
            .openAt(diary.getOpenAt())
            .createdAt(diary.getCreatedAt())
            .updatedAt(diary.getUpdatedAt())
            .build();
    }
}
