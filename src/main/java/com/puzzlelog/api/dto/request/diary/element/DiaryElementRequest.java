package com.puzzlelog.api.dto.request.diary.element;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryElementRequest {

    @NotBlank(message = "요소 타입은 필수입니다.")
    private String elementType;  // TEXT, IMAGE, VIDEO, AUDIO, STICKER, DRAWING

    private String contentId;     // DRAWING일 때 null 허용
    private String drawingData;   // DRAWING 타입일 때만 필수

    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);

    @Builder.Default
    private Double scale = 1.0;

    @Builder.Default
    private Double rotation = 0.0;

    private ElementDecorationRequest decoration;  
    
    // DTO 레벨의 간단한 타입별 검증 메서드
    public boolean isValidByType() {
        if ("DRAWING".equals(elementType)) {
            return drawingData != null && !drawingData.isBlank() && (contentId == null || contentId.isBlank());
        } else {
            return contentId != null && !contentId.isBlank() && (drawingData == null || drawingData.isBlank());
        }
    }
}
