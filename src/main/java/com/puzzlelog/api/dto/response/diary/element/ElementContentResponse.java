package com.puzzlelog.api.dto.response.diary.element;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dao.document.Piece;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementContentResponse {
    private String contentId; // Asset 또는 Piece의 ID
    private String elementType; // TEXT, IMAGE, VIDEO, AUDIO, STICKER 등

    // 타입별 상세 데이터
    private String text;       // TEXT 콘텐츠일 경우
    private String url;        // IMAGE, VIDEO, AUDIO, STICKER의 경우
    private String name;       // Asset의 경우 name
    private GeoJsonPoint location; // 위치 데이터, Piece에서 사용 가능
    private List<String> tags;     // Piece에서 사용 가능
    
 // Piece에서 생성 (mediaId가 이미 URL인 경우)
    public static ElementContentResponse from(Piece piece) {
        return ElementContentResponse.builder()
            .contentId(piece.getId())
            .elementType(piece.getType())
            .text("TEXT".equals(piece.getType()) ? piece.getContent() : null)
            .url(List.of("IMAGE", "AUDIO", "VIDEO").contains(piece.getType()) 
                ? piece.getMediaId()
                : null)
            .location(piece.getLocation())
            .tags(piece.getTags())
            .build();
    }

    // Asset에서 생성 (imageUrl 그대로 사용)
    public static ElementContentResponse from(Asset asset) {
        return ElementContentResponse.builder()
            .contentId(asset.getId())
            .elementType(asset.getType())
            .name(asset.getName())
            .url(asset.getImageUrl())
            .build();
    }
}
