package com.puzzlelog.api.dto.response.diary.meta;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementResponse;

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
public class DiaryDetailResponse {
    private String diaryId;
    private String userId;
    private String title;
    private String backgroundContentId;
    private String themeColor;
    private String emotionContentId;
    private Boolean isShared;
    private Instant openAt;  // 타임캡슐 여부 (없으면 null)
    private Instant createdAt;
    private Instant updatedAt;

    private List<DiaryElementResponse> elements;  // 요소 상세 정보 리스트
    
    public static DiaryDetailResponse from(Diary diary, List<DiaryElement> elements) {
        return DiaryDetailResponse.builder()
            .diaryId(diary.getId())
            .userId(diary.getUserId())
            .title(diary.getTitle())
            .backgroundContentId(diary.getBackgroundContentId())
            .themeColor(diary.getThemeColor())
            .emotionContentId(diary.getEmotionContentId())
            .isShared(diary.getIsShared())
            .createdAt(diary.getCreatedAt())
            .updatedAt(diary.getUpdatedAt())
            .openAt(diary.getOpenAt())
            .elements(elements.stream()
                .map(DiaryElementResponse::from)
                .collect(Collectors.toList()))
            .build();
    }
}
