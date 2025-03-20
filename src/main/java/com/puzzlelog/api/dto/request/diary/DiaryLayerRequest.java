package com.puzzlelog.api.dto.request.diary;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryLayerRequest {

    @NotBlank(message = "레이어 타입은 필수입니다.")
    private String pieceType;

    private String contentId;
    private String drawingData;
    
    @NotNull(message = "레이어 순서는 명시적으로 지정되어야 합니다.")
    private Integer layerOrder;  // 항상 명시하도록 필수로 지정 (권장)

    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);

    @Builder.Default
    private Double scale = 1.0;

    @Builder.Default
    private Double rotation = 0.0;

    private PieceDecorationRequest decoration;  
}
