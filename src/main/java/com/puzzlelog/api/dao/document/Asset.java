package com.puzzlelog.api.dao.document;

import java.util.List;
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
    private String type;        // STICKER, BACKGROUND, EMOTION
    private String mediaId;     // Cloudinary에서 제공한 미디어 URL
    private String publicId;    // Cloudinary의 publicId
    private List<String> tags;  // 태그 정보 (배열 형태)
    
    @Builder.Default
    private boolean deleted = false;
}
