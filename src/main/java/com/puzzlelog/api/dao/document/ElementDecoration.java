package com.puzzlelog.api.dao.document;

import java.util.List;

import com.puzzlelog.api.dto.request.diary.element.ElementDecorationRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DiaryElement의 스타일, 효과, 편집 정보 등을 관리하는 클래스입니다.
 * 요소의 외형과 멀티미디어 특수 효과, 글꼴 설정 등 세부 스타일을 정의합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementDecoration {

    // ✅ 공통 스타일 (모든 요소에 적용 가능)

    /** 테두리 색상 (Hex 코드 또는 색상명, 옵션) */
    private String borderColor;

    /** 투명도 (0.0~1.0, 1이 불투명, 0이 완전 투명, 옵션) */
    private Double opacity;

    /** 모서리 둥글기 (픽셀 단위, 옵션) */
    private Double borderRadius;

    // ✅ 텍스트(TEXT) 요소 전용 스타일

    /** 글꼴 이름 (옵션) */
    private String font;

    /** 글자 크기 (픽셀 단위, 옵션) */
    private Integer fontSize;

    /** 글자 색상 (Hex 코드 또는 색상명, 옵션) */
    private String color;

    /**
     * 글꼴 스타일 (옵션)
     * 가능한 값: ["bold", "italic", "underline", "strike"]
     */
    private List<String> fontStyle;

    /**
     * 텍스트 정렬 방식 (옵션)
     * 가능한 값: "left", "center", "right"
     */
    private String align;

    // ✅ 이미지(IMAGE), 비디오(VIDEO) 요소 공통 스타일

    /**
     * 크롭(자르기) 정보 [width, height, x, y, gravity] (옵션)
     * 예시: [100, 100, 10, 20, "center"]
     * gravity(기준점): "center", "top", "bottom", "left", "right"
     */
    private List<Object> crop;

    // ✅ 오디오(AUDIO), 비디오(VIDEO) 요소 공통 스타일

    /** 미디어 재생 시작 위치 (초 단위, 옵션) */
    private Double startOffset;

    /** 미디어 재생 끝 위치 (초 단위, 옵션) */
    private Double endOffset;

    /** 볼륨 크기 (0~100%, 옵션) */
    private Integer volume;

    // ✅ 부가 효과 (외부 라이브러리 또는 AI 기반 특수 효과, 옵션)

    /**
     * 적용할 특수 효과 목록 (옵션)
     * 가능한 값 예시: ["blur", "background_removal", "recolor"]
     */
    private List<String> effects;

    /**
     * ElementDecorationRequest에서 ElementDecoration으로 변환하는 유틸리티 메서드
     *
     * @param request ElementDecorationRequest 객체
     * @return ElementDecoration 인스턴스
     */
    public static ElementDecoration from(ElementDecorationRequest request) {
        if (request == null) {
            return null;
        }

        return ElementDecoration.builder()
            .borderColor(request.getBorderColor())
            .opacity(request.getOpacity())
//            .borderRadius(request.getBorderRadius())
            .font(request.getFont())
            .fontSize(request.getFontSize())
            .color(request.getColor())
            .fontStyle(request.getFontStyle())
            .align(request.getAlign())
            .crop(request.getCrop())
            .startOffset(request.getStartOffset())
            .endOffset(request.getEndOffset())
            .volume(request.getVolume())
            .effects(request.getEffects())
            .build();
    }
}
