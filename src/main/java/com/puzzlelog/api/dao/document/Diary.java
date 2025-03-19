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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diaries")
public class Diary {

    @Id
    private String id;

    private String userId;
    private String title;
    
    // 일기 내부 요소
    private List<String> layerIds; // DiaryLayer 컬렉션의 ID 리스트 (레이어 순서대로 저장)
    
    // 배경 설정
    private String backgroundContentId; // 이미지 Content Id, null 가능

    private String themeColor;
    private String emotion;

    @Builder.Default
    private Boolean isShared = false;
    
    @CreatedDate
    private Instant createdAt;
    private Instant updatedAt;
    
    @Builder.Default
    private Boolean isDeleted = false;

    // 타임캡슐에 사용
    private Instant openAt;
}
