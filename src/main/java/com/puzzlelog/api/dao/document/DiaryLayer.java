package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
@Document(collection = "diary_layers")
public class DiaryLayer {

    @Id
    private String layerId;      // 자동으로 생성된 ObjectId 값을 문자열로 저장 [클라이언트의 UUID와는 다른 서버의 ObjectId]

    // 조각 개인정보
    private String diaryId;      // 소속된 Diary ID
    private String pieceType;    // PIECE(TEXT, IMAGE, VIDEO, AUDIO), STICKER, DRAWING
    private String contentId;    // Piece, Sticker의 원본 콘텐츠 ID (DRAWING은 null 가능)
    
    private String drawingData;      // DRAWING 타입일 때만 SVG 데이터 저장, 그 외는 null
    
    // 필수 기본 변형 요소들 (위치 및 변형 정보)
    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);  // 기본값 [0.0, 0.0] - [x, y]
    @Builder.Default
    private Double scale = 1.0;                         // 기본값 1.0 - size
    @Builder.Default
    private Double rotation = 0.0;                      // 기본값 0.0 (회전 없음) - angle

    private PieceDecoration decoration; // 확장 편집 요소
    
    private Integer layerOrder;   // 우선순위 (값이 높을수록 위에 표시)

    @CreatedDate
    private Instant createdAt;
    private Instant updatedAt;
}