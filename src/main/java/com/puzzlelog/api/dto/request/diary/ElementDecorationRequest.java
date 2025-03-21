package com.puzzlelog.api.dto.request.diary;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementDecorationRequest {
    // TEXT 스타일
    private String font;
    private Integer fontSize;
    private String color;
    private List<String> fontStyle;
    private String align;

    // IMAGE, VIDEO 스타일
    private List<Object> crop; // [width, height, x, y, gravity]
    private List<String> effects;
    private String borderColor;
    private Double opacity;

    // AUDIO 스타일
    private Double startOffset;
    private Double endOffset;
    private Integer volume;
}
