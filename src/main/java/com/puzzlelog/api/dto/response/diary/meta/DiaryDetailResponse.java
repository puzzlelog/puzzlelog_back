package com.puzzlelog.api.dto.response.diary.meta;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dto.response.asset.AssetResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementResponse;
import com.puzzlelog.api.dto.response.diary.element.ElementContentResponse;

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

    private AssetResponse background;  // 배경 상세 정보 포함
    private String themeColor;
    private AssetResponse emotion;     // 이모션 상세 정보 포함

    private Boolean shared;
    private Instant openAt; // 타임캡슐 여부 없으면 null
    private Instant createdAt;
    private Instant updatedAt;

    private List<DiaryElementResponse> elements;

    public static DiaryDetailResponse from(
            Diary diary,
            Asset background,
            Asset emotion,
            List<DiaryElement> elements,
            Map<String, ElementContentResponse> contentMap) {

        return DiaryDetailResponse.builder()
            .diaryId(diary.getId())
            .userId(diary.getUserId())
            .title(diary.getTitle())
            .background(AssetResponse.from(background))
            .themeColor(diary.getThemeColor())
            .emotion(AssetResponse.from(emotion))
            .shared(diary.isShared())
            .openAt(diary.getOpenAt())
            .createdAt(diary.getCreatedAt())
            .updatedAt(diary.getUpdatedAt())
            .elements(elements.stream()
                .map(element -> DiaryElementResponse.from(
                    element, 
                    contentMap.get(element.getContentId())  // ✅ 여기서 content 전달!
                ))
                .collect(Collectors.toList()))
            .build();
    }

}
