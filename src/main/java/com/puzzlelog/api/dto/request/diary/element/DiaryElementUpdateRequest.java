package com.puzzlelog.api.dto.request.diary.element;

import java.util.List;

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
public class DiaryElementUpdateRequest {

    private String contentId;     // 동일 타입 내 변경 가능
    private String drawingData;   // DRAWING 타입만 가능
    private String date;          // DATE 타입만 가능 (YYYY-MM-DD 형식)

    private List<Double> position;
    private List<Double> size;
    private Double scale;
    private Double rotation;
    private ElementDecorationRequest decoration;
}