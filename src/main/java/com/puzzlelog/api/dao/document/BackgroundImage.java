package com.puzzlelog.api.dao.document;

import java.time.Instant;

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
@Document(collection = "background_images")
public class BackgroundImage {

    @Id
    private String id;  // 배경 이미지 contentId
    private String imageUrl;  // 실제 이미지 URL
    @CreatedDate  // 생성일 자동 입력
    private Instant createdAt;
}
