package com.puzzlelog.api.dto.response.diary.element;

import java.time.Instant;
import java.util.List;

import com.puzzlelog.api.dao.document.DiaryElement;

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
public class DiaryElementResponse {
    private String id;
    private String elementType;
    private String contentId; 
    private String drawingData;
    private List<Double> position;
    private Double scale;
    private Double rotation;
    private Instant createdAt;
    private Instant updatedAt;

    private ElementDecorationResponse decoration; // 일단 null로 반환, 나중에 사용 예정
    
    public static DiaryElementResponse from(DiaryElement element) {
        return DiaryElementResponse.builder()
            .id(element.getId())
            .elementType(element.getElementType())
            .contentId(element.getContentId())
            .drawingData(element.getDrawingData())
            .position(element.getPosition())
            .scale(element.getScale())
            .rotation(element.getRotation())
            .createdAt(element.getCreatedAt())
            .updatedAt(element.getUpdatedAt())
            .decoration(null) // 현재는 null (추후 구현 예정)
            .build();
    }
}
