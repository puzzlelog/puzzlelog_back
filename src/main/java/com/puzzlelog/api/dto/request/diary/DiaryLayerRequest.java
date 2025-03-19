package com.puzzlelog.api.dto.request.diary;

import java.util.List;

import javax.validation.constraints.NotBlank;

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
public class DiaryLayerRequest {
    @NotBlank(message = "레이어 타입은 필수입니다.")
    private String pieceType;    // PIECE, STICKER, DRAWING (필수)

    private String contentId;    // DRAWING 아닐때 필수, DRAWING일때 null 허용
    private String drawingData;  // DRAWING일때 필수

    // 필수 기본 변형 요소들
    private List<Double> position;  // [x, y]
    private Double scale;           // 기본값 1.0
    private Double rotation;        // 기본값 0.0

    private PieceDecorationRequest decoration; // 선택
    private Integer layerOrder;     // 필수
}
