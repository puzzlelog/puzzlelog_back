package com.puzzlelog.api.dto.response.diary.element;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dao.document.Piece;
import lombok.*;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementContentResponse {
    private String id;
    private String type;             // TEXT, IMAGE, AUDIO, VIDEO, STICKER, EMOTION, BACKGROUND
    private String text;          	// TEXT 타입일 경우
    private String mediaId;          // IMAGE, AUDIO, VIDEO, STICKER, EMOTION, BACKGROUND
    private String publicId;         // Cloudinary의 publicId (선택)
    private List<String> tags;       // 태그 정보
    private GeoJsonPoint location;   // 위치 정보 (Piece에서만 사용)
    private String name;             // Asset인 경우 이름

    // Piece에서 생성
    public static ElementContentResponse from(Piece piece) {
        return ElementContentResponse.builder()
                .id(piece.getId())
                .type(piece.getType())
                .text("TEXT".equals(piece.getType()) ? piece.getText() : null)
                .mediaId(piece.getMediaId())
                .publicId(piece.getPublicId())
                .tags(piece.getTags())
                .location(piece.getLocation())
                .build();
    }

    // Asset에서 생성
    public static ElementContentResponse from(Asset asset) {
        return ElementContentResponse.builder()
                .id(asset.getId())
                .type(asset.getType())
                .name(asset.getName())
                .mediaId(asset.getMediaId())
                .publicId(asset.getPublicId())
                .tags(asset.getTags())
                .build();
    }
}
