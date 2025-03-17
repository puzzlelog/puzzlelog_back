package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.puzzlelog.api.dao.document.Piece.Type;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateRequest {

    private Type type; // 변경될 수 있음 (선택적)

    private String content; // TEXT 타입 시 필수

    private List<String> tags; // 선택적

    private GeoJsonPoint location; // 선택적

    private Boolean isPrivate; // 선택적
}
