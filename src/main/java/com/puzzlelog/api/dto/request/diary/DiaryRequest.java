package com.puzzlelog.api.dto.request.diary;

import com.puzzlelog.api.dao.document.Diary.DiaryType;
import com.puzzlelog.api.dao.document.Diary.PieceType;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryRequest {
	private String userId;
    private String title;
    private DiaryType type;
    private List<DiaryPieceRequest> pieces;
    private String themeColor;
    private Boolean isShared;
    private Instant openAt;  // 타임캡슐인 경우만 필요 (선택)

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryPieceRequest {
        private String pieceId;
        private PieceType pieceType;
        private PieceDecorationRequest decoration;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceDecorationRequest {
        private PositionRequest position;
        private Double scale;
        private RotationRequest rotation;

        private TextStyleRequest textStyle;
        private ImageStyleRequest imageStyle;
        private VideoStyleRequest videoStyle;
        private AudioStyleRequest audioStyle;
    }

    // TEXT 전용 요청 스타일
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextStyleRequest {
        private String font;
        private Integer fontSize;
        private String color;
        private Boolean bold;
        private Boolean italic;
        private String align;
    }

    // IMAGE 전용 요청 스타일
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageStyleRequest {
        private CropRequest crop;
        private List<String> effects;
        private String borderColor;
        private Double opacity;
    }

    // VIDEO 전용 요청 스타일
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoStyleRequest {
        private CropRequest crop;
        private List<String> effects;
        private String borderColor;
        private Double opacity;
    }

    // AUDIO 전용 요청 스타일
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioStyleRequest {
        private Double startOffset;
        private Double endOffset;
        private Integer volume;
    }

    // 공통 Crop 요청
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CropRequest {
        private Integer width;
        private Integer height;
        private Integer x;
        private Integer y;
        private String gravity;
    }

    // 위치 요청 공통
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionRequest {
        private Double x;
        private Double y;
    }

    // 회전 요청 공통
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RotationRequest {
        private Double angle;
    }
}
