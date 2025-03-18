package com.puzzlelog.api.dao.document;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "user_history")
@Data
@Builder
public class UserHistory {
    @Id
    private String id;

    private String userId;           // 변경 대상 사용자 ID
    private String action;           // 변경 행동 (UPDATE, DELETE, BLOCKED 등)
    private String reason;           // 변경 이유 (관리자용, optional)
    private String changedBy;
    private LocalDateTime timestamp; // 변경 시간
    private Map<String, Object> changedFields; // 변경된 필드와 값(optional)
}