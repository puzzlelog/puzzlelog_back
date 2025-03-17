package com.puzzlelog.api.dto.response.piece;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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
public class PieceResponse {
    private String id;
    private Integer userId;
    private String type;
    private String content;
    private List<String> tags;
    private GeoJsonPoint location;
    private String mediaId;
    private String publicId;
    private Boolean isPrivate;
    private Instant createdAt;

    public static PieceResponse from(Piece piece) {
        return PieceResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .type(piece.getType().name())
                .content(piece.getContent())
                .tags(piece.getTags())
                .location(piece.getLocation())
                .mediaId(piece.getMediaId())
                .publicId(piece.getPublicId())
                .isPrivate(piece.getIsPrivate())
                .createdAt(piece.getCreatedAt())
                .build();
    }
}
