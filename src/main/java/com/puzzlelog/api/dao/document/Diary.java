package com.puzzlelog.api.dao.document;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diaries")
public class Diary {

    @Id
    private ObjectId id;

    private Integer userId; // MySQL 사용자 테이블과 연결된 사용자 ID

    private String title; // 일기 제목

    private List<ObjectId> pieceIds; // 연결된 조각의 ID들

    private String themeColor; // AI 감정 분석을 기반으로 설정된 테마 색상 (HEX)

    @Builder.Default
    private Boolean isShared = false; // 공개 여부 (기본값 false)

    @CreatedDate
    private Instant createdAt; // 생성된 날짜 (자동 설정)
}
