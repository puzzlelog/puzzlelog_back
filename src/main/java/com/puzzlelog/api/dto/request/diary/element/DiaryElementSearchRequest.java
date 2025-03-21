package com.puzzlelog.api.dto.request.diary.element;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryElementSearchRequest {
    private String elementType;  // 필터링할 요소 타입 (TEXT, IMAGE, AUDIO, VIDEO, STICKER, DRAWING)
    private int page;            // 페이징 페이지 번호
    private int size;            // 페이지 크기
}