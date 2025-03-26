package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateRequest {

    private String type; // "TEXT", "IMAGE", "VIDEO", "AUDIO"

    private String text; // TEXT 타입 시 필수

    private List<String> tags; // 선택적

    private GeoJsonPoint location; // 선택적

    private Boolean privatePiece; // 선택적
}
