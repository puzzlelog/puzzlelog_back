package com.puzzlelog.api.dao.document;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 임베디드 클래스 (DiaryLayer의 일부에 속하는 클래스, DiaryLayer를 참조하면 이 부분도 참조한다.)
public class PieceDecoration {
    // TEXT 스타일
    private String font; // 글씨체 (웹 폰트 사용)
    private Integer fontSize;
    private String color;
    private List<String> fontStyle; // [bold, italic ... ]
    private String align;

    // IMAGE, VIDEO 스타일 (+ 추가 존재)
    private List<Object> crop;     // [width, height, x, y, gravity]
    private List<String> effects;
    private String borderColor;
    private Double opacity;

    // AUDIO 스타일 (+추가 존재)
    private Double startOffset;
    private Double endOffset;
    private Integer volume;
}
