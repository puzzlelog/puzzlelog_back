package com.puzzlelog.api.dao.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "stickers")
public class Sticker {

    @Id
    private String id;         // 스티커의 고유 contentId
    private String imageUrl;   // 스티커 이미지 URL
    private String name;       // 스티커 이름이나 설명 (선택적)

    @CreatedDate
    private Instant createdAt;
}