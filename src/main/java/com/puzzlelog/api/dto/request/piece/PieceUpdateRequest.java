package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import lombok.*;

/**
 * 조각 수정 요청 DTO입니다.
 * 요청자는 수정하고자 하는 필드를 선택적으로 포함할 수 있으며,
 * 서버에서는 권한에 따라 수정 가능 항목을 제한합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateRequest {

    /**
     * 조각의 타입 (선택 수정)
     * 허용 값: "TEXT", "IMAGE", "VIDEO", "AUDIO"
     * - 본인만 수정 가능
     */
    private String type;

    /**
     * 텍스트 내용 (TEXT 타입일 경우에만 사용)
     * - 본인만 수정 가능
     */
    private String text;

    /**
     * 조각에 포함된 태그 목록
     * - 본인 또는 관리자 모두 수정 가능
     */
    private List<String> tags;

    /**
     * 조각의 위치 정보 (GeoJSON 포맷)
     * - 본인만 수정 가능
     */
    private GeoJsonPoint location;

    /**
     * 조각의 비공개 여부 (true = 비공개, false = 공개)
     * - 본인만 수정 가능
     */
    private Boolean privatePiece;
}
