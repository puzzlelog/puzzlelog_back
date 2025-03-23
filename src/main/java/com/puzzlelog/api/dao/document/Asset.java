package com.puzzlelog.api.dao.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "assets")
public class Asset {
    @Id
    private String id;
    private String name;
    private String type;
    private String imageUrl;
    private boolean deleted;
}
