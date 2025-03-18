package com.puzzlelog.api.dao.document;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diaries")
public class Diary {

    @Id
    private ObjectId id;

    private String userId;            // 작성자의 사용자 ID (MySQL 사용자 테이블과 연결)
    private String title;             // 일기 제목
    private DiaryType type;           // DIARY 또는 TIMECAPSULE
    private List<DiaryPiece> pieces;  // 꾸며진 조각들의 리스트

    private String themeColor;        // AI 감정 분석 기반 테마 색상 (HEX 코드)

    @Builder.Default
    private Boolean isShared = false; // 공개 여부 (기본값 false)
    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부

    @CreatedDate
    private Instant createdAt;        // 생성된 날짜 (자동 설정)
    private Instant updatedAt;        // 수정된 날짜
    private Instant openAt;           // 타임캡슐 공개 시간 (타임캡슐 전용)

    
    public enum DiaryType {
        DIARY, TIMECAPSULE
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryPiece {
        private String pieceId;               // 조각 ID (MongoDB의 Piece 참조)
        private PieceDecoration decoration;   // 꾸밈 요소 (폰트, 크기, 위치 등)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceDecoration {
        // 공통적인 꾸밈 요소
        private Position position;        // 위치(x,y 좌표)
        private Double scale;             // 크기 조정값
        private Rotation rotation;        // 회전값

        // TEXT 전용 꾸밈 요소 (텍스트에만 적용됨)
        private TextStyle textStyle;

        // 미디어 타입 (IMAGE, VIDEO, AUDIO)에만 적용되는 꾸밈 요소
        private MediaStyle mediaStyle;
    }

    // 텍스트 전용 스타일 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextStyle {
        private String font;              // 글씨체
        private Integer fontSize;         // 폰트 크기
        private String color;             // 글자 색상
        private Boolean bold;             // 볼드 여부
        private Boolean italic;           // 이탤릭 여부
        private String align;             // 정렬 (left, center, right 등)
    }

    // 미디어 전용 스타일 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MediaStyle {
        private String borderColor;       // 테두리 색상
        private Double opacity;           // 투명도
        private String filter;            // CSS 필터 효과 (blur, grayscale 등)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position { // 조각 위치
        private Double x;
        private Double y;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Rotation { // 회전값
        private Double angle;
    }
}