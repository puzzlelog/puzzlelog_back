package com.puzzlelog.api.dao.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "stickers")
public class Sticker {
    @Id
    private String id;
    private String name;
    private String type;
    private String imageUrl;
    private boolean isDeleted;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
