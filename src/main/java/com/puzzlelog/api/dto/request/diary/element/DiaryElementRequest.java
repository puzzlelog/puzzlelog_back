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
    private String elementType;  // TEXT, IMAGE, VIDEO, AUDIO, STICKER, DRAWING, DATE

    private String contentId;     // TEXT, IMAGE, VIDEO, AUDIO, STICKER 일 때 필수
    private String drawingData;   // DRAWING 타입일 때만 필수
    private String date;          // DATE 타입일 때만 필수 (예: "2025-03-21")

    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);

    @Builder.Default
    private Double scale = 1.0;

    @Builder.Default
    private Double rotation = 0.0;

    private ElementDecorationRequest decoration;  
    
    // DTO 레벨의 간단한 타입별 검증 메서드
    public boolean isValidByType() {
        switch (elementType) {
            case "DRAWING":
                return drawingData != null && !drawingData.isBlank()
                    && contentId == null && date == null;

            case "DATE":
                // 간단한 YYYY-MM-DD 형태만 체크
                return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}")
                    && contentId == null && drawingData == null;

            default: // TEXT, IMAGE, VIDEO, AUDIO, STICKER
                return contentId != null && !contentId.isBlank()
                    && drawingData == null && date == null;
        }
    }
    
    public String validateAndGetMessage() {
        switch (elementType) {
            case "DRAWING":
                if (drawingData == null || drawingData.isBlank())
                    return "DRAWING 타입에서 drawingData는 필수입니다.";
                if (contentId != null && !contentId.isBlank())
                    return "DRAWING 타입에서는 contentId를 사용할 수 없습니다.";
                if (date != null && !date.isBlank())
                    return "DRAWING 타입에서는 date를 사용할 수 없습니다.";
                break;

            case "DATE":
                if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}"))
                    return "DATE 타입에서 date는 필수이며, YYYY-MM-DD 형식이어야 합니다.";
                if (contentId != null && !contentId.isBlank())
                    return "DATE 타입의 요소만 날짜를 지정할 수 있습니다.";
                if (drawingData != null && !drawingData.isBlank())
                    return "DATE 타입에서는 drawingData를 사용할 수 없습니다.";
                break;

            case "TEXT":
            case "IMAGE":
            case "AUDIO":
            case "VIDEO":
            case "STICKER":
                if (contentId == null || contentId.isBlank())
                    return elementType + " 타입에서 contentId는 필수입니다.";
                if (drawingData != null && !drawingData.isBlank())
                    return elementType + " 타입에서는 drawingData를 사용할 수 없습니다.";
                if (date != null && !date.isBlank())
                    return "DATE 타입의 요소만 날짜를 지정할 수 있습니다.";
                break;

            default:
                return "허용되지 않는 요소 타입입니다: " + elementType;
        }

        return null; // 유효한 경우 null 반환
    }
}
