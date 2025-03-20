package com.puzzlelog.api.dto.request.diary;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "일기 제목은 필수입니다.")
    private String title;

    private String backgroundContentId; // 배경 이미지 콘텐츠 ID (선택)
    private String themeColor;          // 선택
    private String emotion;             // 감정 상태 콘텐츠 ID (선택)
    
    @Builder.Default
    private Boolean isShared = false;   // 기본값 설정 (선택)

    private String openAt;     // 날짜 ("2027-06-30" 형태) (타임캡슐용)
    private String timeZone;   // 사용자 타임존 (선택적, 없으면 한국으로 처리)

    @NotEmpty(message = "일기에는 최소 하나 이상의 레이어가 있어야 합니다.")
    private List<DiaryLayerRequest> layers;  // 최소 1개 이상 필수
}
