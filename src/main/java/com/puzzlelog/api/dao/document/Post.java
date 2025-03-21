package com.puzzlelog.api.dao.document;

import java.time.LocalDateTime;

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
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String userId;
    private String title;
    private String content;
    
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean isLiked = false;
    @Builder.Default
    private int likesCount = 0;
    @Builder.Default
    private boolean isDeleted = false;
}
