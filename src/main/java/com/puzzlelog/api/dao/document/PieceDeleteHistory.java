package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 조각 변경 이력 도큐먼트
 * - 수정, 삭제, 복원, 관리자 조회 등의 이력을 기록합니다.
 * - 일반 사용자 조회는 기록하지 않으며, 관리자에 의한 조회만 로그로 남깁니다.
 */
@Document(collection = "piece_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceDeleteHistory {

    /** 조각 ID */
    private String pieceId;

    /** 조각 원소유자 ID */
    private String ownerId;

    /** 작업 수행자 ID (본인 또는 관리자) */
    private String performedBy;

//    /** 작업 유형 (MODIFY, DELETE, RESTORE, VIEW) */
//    private String action;
    
    private String deletedBy;

    /** 작업 사유 (삭제/복원 사유, 수정 설명 등) */
    private String reason;

    /** 변경된 필드 목록 (수정 시 사용) */
    private Map<String, Object> changedFields;

    /** 작업 시각 (UTC 기준) */
    @CreatedDate
    @Field("timestamp")
    private Instant timestamp;
}
