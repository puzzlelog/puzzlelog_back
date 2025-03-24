package com.puzzlelog.api.dto.request.diary.meta;

import java.time.Instant;

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
public class DiaryMetaUpdateRequest {
    private String title;
    private String backgroundContentId;
    private String themeColor;
    private String emotionContentId;
    private Boolean shared;
    private Instant openAt;
}
