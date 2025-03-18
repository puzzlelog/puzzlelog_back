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

    private String userId;
    private String title;
    private DiaryType type;
    private List<DiaryPiece> pieces;

    private String themeColor;

    @Builder.Default
    private Boolean isShared = false;
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    private Instant createdAt;
    private Instant updatedAt;
    private Instant openAt;

    public enum DiaryType {
        DIARY, TIMECAPSULE
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryPiece {
        private String pieceId;
        private PieceType pieceType;        // 타입 구분
        private PieceDecoration decoration; // 꾸밈 요소
    }

    public enum PieceType {
        TEXT, IMAGE, VIDEO, AUDIO
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceDecoration {
        private Position position;
        private Double scale;
        private Rotation rotation;

        private TextStyle textStyle;       // TEXT 타입 전용
        private ImageStyle imageStyle;     // IMAGE 타입 전용
        private VideoStyle videoStyle;     // VIDEO 타입 전용
        private AudioStyle audioStyle;     // AUDIO 타입 전용
    }

    // TEXT 전용 스타일
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextStyle {
        private String font;
        private Integer fontSize;
        private String color;
        private Boolean bold;
        private Boolean italic;
        private String align;
    }

    // IMAGE 전용 스타일 (Cloudinary 기능에 맞춤)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageStyle {
        private Crop crop;                 // 크롭 정보
        private List<String> effects;      // 특수 효과 필터 (blur, grayscale 등)
        private String borderColor;        // 테두리 색상
        private Double opacity;            // 투명도
    }

    // VIDEO 전용 스타일 (Cloudinary 기능에 맞춤)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoStyle {
        private Crop crop;                 // 크롭 정보
        private List<String> effects;      // 특수 효과 (reverse, accelerate 등)
        private String borderColor;        // 테두리 색상
        private Double opacity;            // 투명도
    }

    // AUDIO 전용 스타일 (Cloudinary 기능에 맞춤)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioStyle {
        private Double startOffset;        // 시작 지점(초)
        private Double endOffset;          // 종료 지점(초)
        private Integer volume;            // 볼륨 조절(%, 기본 100)
    }

    // 크롭 정보 (IMAGE, VIDEO 공통)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Crop {
        private Integer width;
        private Integer height;
        private Integer x;
        private Integer y;
        private String gravity; // 크롭 기준 (center, north, south 등)
    }

    // 위치 공통
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position {
        private Double x;
        private Double y;
    }

    // 회전 공통
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Rotation {
        private Double angle;
    }
}