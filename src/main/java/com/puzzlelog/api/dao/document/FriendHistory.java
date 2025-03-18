package com.puzzlelog.api.dao.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "friend_history")
@Data
@Builder
public class FriendHistory {
    @Id
    private String id;

    private String userId;
    private String friendId;
    private String status;
    private LocalDateTime timestamp; // 상태 변경 시간
}