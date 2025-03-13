package com.puzzlelog.api.dto.response;

import com.puzzlelog.api.dao.document.Piece;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import lombok.*;
import java.time.Instant;

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
    private GeoJsonPoint location;
    private String mediaId;
    private Boolean isPrivate;
    private Instant createdAt;

    public static PieceResponse from(Piece piece) {
        return PieceResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .type(piece.getType().name())
                .content(piece.getContent())
                .location(piece.getLocation())
                .mediaId(piece.getMediaId())
                .isPrivate(piece.getIsPrivate())
                .createdAt(piece.getCreatedAt())
                .build();
    }
}
