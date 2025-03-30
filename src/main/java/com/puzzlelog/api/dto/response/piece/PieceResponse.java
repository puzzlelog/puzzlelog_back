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

/**
 * 조각 단일 조회 응답 DTO입니다.
 * 클라이언트에게 조각 정보를 반환할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceResponse {

    /** 조각 ID (MongoDB의 _id) */
    private String id;

    /** 조각을 생성한 사용자 ID */
    private String userId;

    /** 조각 타입 (TEXT, IMAGE, VIDEO, AUDIO 중 하나) */
    private String type;

    /** 텍스트 내용 (type이 TEXT일 경우에만 사용) */
    private String text;

    /** 조각에 포함된 태그 목록 */
    private List<String> tags;

    /** 조각 생성 시의 위치 정보 (위도/경도) */
    private GeoJsonPoint location;

    /** Cloudinary에 저장된 미디어의 URL */
    private String mediaId;

    /** Cloudinary의 파일 식별자 (publicId) */
    private String publicId;

    /** 조각의 비공개 여부 (true = 비공개, false = 공개) */
    private boolean privatePiece;

    /** 조각이 생성된 시간 (UTC 기준) */
    private Instant createdAt;

    /**
     * Piece 엔티티로부터 PieceResponse 객체를 생성합니다.
     *
     * @param piece MongoDB의 Piece 도큐먼트
     * @return 변환된 PieceResponse 객체
     */
    public static PieceResponse from(Piece piece) {
        return PieceResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .type(piece.getType())
                .text(piece.getText())
                .tags(piece.getTags())
                .location(piece.getLocation())
                .mediaId(piece.getMediaId())
                .publicId(piece.getPublicId())
                .privatePiece(piece.isPrivatePiece())
                .createdAt(piece.getCreatedAt())
                .build();
    }
    
    public static PieceResponse forAdminPreview(Piece piece) {
        return PieceResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .type(piece.getType())
                .tags(piece.getTags())
                .location(piece.getLocation())
                .privatePiece(true)
                .createdAt(piece.getCreatedAt())
                .build();
    }
}
