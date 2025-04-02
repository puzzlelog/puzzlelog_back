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
 * 커뮤니티에서 공유된 일기(Diary)에 해당하는 게시글 정보를 저장하는 Document.
 * 사용자가 일기를 공유하면 자동으로 생성됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 게시글 작성자(일기 공유자)의 사용자 ID */
    private String userId;

    /** 공유된 일기의 MongoDB ID */
    private String diaryId;

    /** 게시글의 제목 (공유된 일기 제목과 동일) */
    private String title;

    /** 게시글의 주요 내용 또는 요약 (일기의 요약 또는 내용 일부) */
    private String content;

    /** 게시글 작성(공유) 시간 (자동 저장) */
    @CreatedDate
    private LocalDateTime createdAt;

    /** 현재 로그인한 사용자의 좋아요 여부 (기본값: false) */
    @Builder.Default
    private boolean liked = false;

    /** 해당 게시글의 총 좋아요 개수 (기본값: 0) */
    @Builder.Default
    private int likesCount = 0;

    /** 게시글의 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;
}
