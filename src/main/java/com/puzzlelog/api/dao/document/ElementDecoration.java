package com.puzzlelog.api.dao.document;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// DiaryElement의 일부로서 요소의 스타일과 편집 정보를 정의합니다.
public class ElementDecoration {
    
    // 공통 스타일 (IMAGE, VIDEO, TEXT 공통)
    private String borderColor;      // 테두리 색상
    private Double opacity;          // 투명도
    private Double borderRadius;     // 둥근 모서리 (Round Corners)

    // TEXT 전용 스타일
    private String font;             // 글꼴
    private Integer fontSize;        // 글씨 크기
    private String color;            // 글씨 색상
    private List<String> fontStyle;  // ["bold", "italic", "underline"...]
    private String align;            // 정렬 ("left", "center", "right")

    // IMAGE, VIDEO 공통 스타일
    private List<Object> crop;       // [width, height, x, y, gravity]

    // AUDIO, VIDEO 공통 스타일
    private Double startOffset;      // 재생 시작 위치 (Trimming)
    private Double endOffset;        // 재생 끝 위치 (Trimming)
    private Integer volume;          // 볼륨 크기 (%)

    // 부가 효과 (외부 라이브러리 또는 AI 기반 특수 효과)
    private List<String> effects;    // ["blur", "background_removal", "recolor"...]
}
