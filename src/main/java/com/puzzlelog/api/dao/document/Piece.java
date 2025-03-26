package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
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
@Document(collection = "pieces")
public class Piece {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 소유자 사용자 ID (MySQL의 user_id 참조) */
    private String userId;

    /**
     * 조각의 유형
     * 가능한 값:
     * - TEXT: 텍스트 조각
     * - IMAGE: 이미지 조각
     * - VIDEO: 동영상 조각
     * - AUDIO: 오디오 조각
     * 기본값: TEXT
     */
    @Builder.Default
    private String type = "TEXT";

    /** 조각의 내용 (type이 TEXT인 경우에만 사용) */
    private String text;

    /** 조각에 추가된 태그 목록 (옵션) */
    private List<String> tags;

    /** 조각이 생성된 위치 정보 (위도/경도 GeoJSON 형식, 옵션) */
    private GeoJsonPoint location;

    /** 미디어 URL 또는 파일 식별자 (Cloudinary URL) */
    private String mediaId;

    /** Cloudinary에서 사용하는 publicId (미디어 관리 용도, 옵션) */
    private String publicId;

    /** 조각의 공개/비공개 여부 (기본값: 공개(false)) */
    @Builder.Default
    private boolean privatePiece = false;

    /** 조각 생성 시간 (자동으로 현재 시간 저장됨) */
    @CreatedDate
    private Instant createdAt;

    /** 조각의 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;
}
