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
    
    private ElementContentResponse content; // 상세정보 추가

    private String drawingData;
    private String date;
    private List<Double> position;
    private List<Double> size;
    private Double scale;
    private Double rotation;
    private Instant createdAt;
    private Instant updatedAt;

    private ElementDecorationResponse decoration; // 현재는 null로 유지

    public static DiaryElementResponse from(DiaryElement element, ElementContentResponse content) {
        return DiaryElementResponse.builder()
            .id(element.getId())
            .elementType(element.getElementType())
            .contentId(element.getContentId())
            .content(content) // 상세 정보 추가
            .drawingData(element.getDrawingData())
            .date(element.getDate())
            .position(element.getPosition())
            .size(element.getSize())
            .scale(element.getScale())
            .rotation(element.getRotation())
            .createdAt(element.getCreatedAt())
            .updatedAt(element.getUpdatedAt())
            .decoration(null) // 추후 구현 예정
            .build();
    }
}
