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

/**
 * DiaryElement는 "조각(Piece)"이나 "에셋(Asset)"을 특정 일기(Diary)에 실제로 배치한 개별 요소입니다.
 * 개념적으로는 Piece나 Asset이 클래스라면, DiaryElement는 이들의 인스턴스(instance)에 해당합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diary_elements")
public class DiaryElement {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 이 요소가 속한 일기의 고유 ID (diaries 컬렉션 참조) */
    private String diaryId;

    /**
     * 요소의 타입
     * 가능한 값:
     * - TEXT: 텍스트 조각
     * - IMAGE: 이미지 조각
     * - AUDIO: 오디오 조각
     * - VIDEO: 비디오 조각
     * - STICKER: 스티커 (에셋의 일종)
     * - DRAWING: 사용자 손 그림 데이터
     * - DATE: 날짜 표시 요소
     */
    private String elementType;

    /**
     * 콘텐츠 ID (Piece나 Asset의 ID를 참조함)
     * elementType이 TEXT, IMAGE, AUDIO, VIDEO, STICKER일 때 사용
     */
    private String contentId;

    /** 사용자 그림 데이터 (elementType이 DRAWING일 때만 사용) */
    private String drawingData;

    /** 날짜 데이터 (elementType이 DATE일 때만 사용, ISO 8601 형식) */
    private String date;

    /** 요소의 위치 [x, y] 좌표값 (기본값: [0.0, 0.0]) */
    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);

    /** 요소의 확대/축소 비율 (기본값: 1.0, 1보다 크면 확대, 작으면 축소) */
    @Builder.Default
    private Double scale = 1.0;

    /** 요소의 회전 각도 (기본값: 0.0, 단위: 도(degree)) */
    @Builder.Default
    private Double rotation = 0.0;

    /** 요소에 적용된 장식(예: 테두리, 그림자 등, 선택적) */
    private ElementDecoration decoration;

    /** 요소의 생성 시각 (자동 저장) */
    @CreatedDate
    private Instant createdAt;

    /** 요소의 마지막 수정 시각 */
    private Instant updatedAt;

    /** 요소의 논리 삭제 여부 (기본값: false) */
    @Builder.Default
    private boolean deleted = false;
}
