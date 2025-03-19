package com.puzzlelog.api.dao.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stickers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sticker {
    @Id
    private String id;
    private String name;
    private String type;
    private String imageUrl;

    @Builder.Default
    private boolean isDeleted = false; // 기본값: false (삭제되지 않음)
}
