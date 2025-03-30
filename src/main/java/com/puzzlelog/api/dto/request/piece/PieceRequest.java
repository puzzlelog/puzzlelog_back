package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 조각 생성 요청 DTO입니다.
 * 클라이언트에서 조각을 생성할 때 필요한 정보를 담습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceRequest {

    /** 사용자 ID (서버에서 JWT로 추출하여 주입) */
    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    /** 조각 타입 (TEXT, IMAGE, VIDEO, AUDIO) */
    @NotNull(message = "타입은 필수입니다.")
    private String type;

    /** 텍스트 내용 (type이 TEXT일 경우 필수) */
    private String text;

    /** 조각에 포함될 태그 목록 (선택) */
    private List<String> tags;

    /** 조각의 위치 정보 (GeoJSON 형식, 선택) */
    private GeoJsonPoint location;

    /** 조각의 공개 여부 (기본값: false, 즉 공개) */
    @Builder.Default
    private Boolean privatePiece = false;
}
