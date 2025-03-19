package com.puzzlelog.api.dto.request.diary;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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
public class DiaryRequest {
    private String userId;      // 필수
    private String title;       // 필수
    private String backgroundContentId; // 선택
    private String themeColor;  // 선택
    private String emotion;     // 선택
    private Boolean isShared;   // 선택 (기본 false)
    private Instant openAt;     // 선택 (타임캡슐용)
    
    @NotEmpty(message = "일기에는 최소 하나 이상의 레이어가 있어야 합니다.")
    private List<DiaryLayerRequest> layers;  // 최소 1개 이상 필수
}
