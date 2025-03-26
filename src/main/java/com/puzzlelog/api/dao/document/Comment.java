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

/**
 * 커뮤니티 게시글(Post)에 작성된 댓글 정보를 저장하는 Document입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "comments")
public class Comment {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 댓글이 작성된 게시글(Post)의 ID (posts 컬렉션 참조) */
    private String postId;

    /** 댓글 작성자의 사용자 ID */
    private String userId;

    /** 댓글의 내용 */
    private String content;

    /** 댓글 작성 시각 (자동 저장) */
    @CreatedDate
    private LocalDateTime createdAt;

    /** 댓글의 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;
}
