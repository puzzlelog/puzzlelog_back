package com.puzzlelog.api.dao.document;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.PrePersist;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "user_history")
@Data
@Builder
public class UserHistory {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 변경 대상 사용자 고유 ID */
    private String userId;

    /**
     * 수행된 행동 타입 (필수)
     * 예시 값:
     * - UPDATE: 사용자 정보 수정
     * - DELETE: 사용자 계정 삭제
     * - BLOCKED: 사용자 차단
     * - UNBLOCKED: 사용자 차단 해제
     */
    private String action;

    /** 변경 이유 (관리자 기록용, 선택적) */
    private String reason;

    /** 변경을 수행한 주체 (관리자 ID 혹은 시스템 자동 수행 등) */
    private String changedBy;

    /** 변경이 수행된 시간 (기본값: 현재 시간 자동 입력) */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** 변경된 필드와 해당 필드의 변경된 값 (선택적) */
    private Map<String, Object> changedFields;

    /** Document가 저장될 때 timestamp 자동 설정 (저장 시점의 시간으로 초기화) */
    @PrePersist
    void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
