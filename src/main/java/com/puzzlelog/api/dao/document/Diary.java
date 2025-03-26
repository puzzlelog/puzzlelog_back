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
    
    /** 일기 내부에 존재하는 요소(Element)
     * 우선 순위가 존재하여 뒤에 있을 수록 위에 존재한다.
     */
    private List<String> elementIds; // DiaryElement 컬렉션의 ID 리스트
    
    // 배경 설정
    private String backgroundContentId; // 이미지 Content Id, null 가능

    private String themeColor;
    private String emotionContentId;  // 감정 상태 콘텐츠의 Content Id (null 가능)

    @Builder.Default
    private boolean shared = false;
    
    @CreatedDate
    private Instant createdAt;
    private Instant updatedAt;
    
    @Builder.Default
    private boolean deleted = false;

    // 타임캡슐에 사용
    private Instant openAt;
}
