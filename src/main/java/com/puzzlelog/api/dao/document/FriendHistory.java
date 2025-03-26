package com.puzzlelog.api.dao.document;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "friend_history")
@Data
@Builder
public class FriendHistory {

    /** MongoDB Document 고유 식별자 */
    @Id
    private String id;

    /** 친구 관계의 주체가 되는 사용자 ID (요청자) */
    private String userId;

    /** 친구 관계의 대상 사용자 ID (상대방) */
    private String friendId;

    /**
     * 친구 관계의 변경된 상태
     *
     * 가능한 값:
     * - PENDING: 친구 요청 상태
     * - ACCEPTED: 친구 요청 수락 상태
     * - DEACTIVATED: 친구 삭제 상태
     * - BLOCKED: 친구 차단 상태
     * - REJECTED: 친구 요청 거절 상태
     */
    private String status;

    /** 친구 상태가 변경된 시점 (기본값: 현재 시간 자동 입력) */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Document 저장 시 자동으로 현재 시간 설정 */
    @PrePersist
    void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
