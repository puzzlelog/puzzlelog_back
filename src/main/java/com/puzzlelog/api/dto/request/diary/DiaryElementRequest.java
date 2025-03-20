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
public class DiaryElementRequest {

    @NotBlank(message = "요소 타입은 필수입니다.")
    private String elementType;  // TEXT, IMAGE, VIDEO, AUDIO, STICKER, DRAWING

    private String contentId;     // DRAWING일 때 null 허용
    private String drawingData;   // DRAWING 타입일 때만 필수

    @NotNull(message = "요소의 순서는 명시적으로 지정되어야 합니다.")
    private Integer elementOrder;

    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);

    @Builder.Default
    private Double scale = 1.0;

    @Builder.Default
    private Double rotation = 0.0;

    private ElementDecorationRequest decoration;  
}
