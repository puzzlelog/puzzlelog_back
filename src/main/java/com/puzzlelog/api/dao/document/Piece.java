package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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
@Document(collection = "pieces")
public class Piece {

    @Id
    private String id;

    private String userId; // MySQL users 테이블의 user_id 참조

    private Type type;  // 조각 유형 (text, image, video, audio)
    
    private String content;  // 조각 내용 (텍스트 형식일 때 사용)

    private List<String> tags;  // 태그 정보 (배열)

    private GeoJsonPoint location;  // 위치 정보

    private String mediaId;
    
    private String publicId;  // ✅ Cloudinary publicId 추가

    @Builder.Default
    private Boolean isPrivate = false;
    
    @CreatedDate
    private Instant createdAt; // 자동 시간 저장

    @Builder.Default
    private Boolean isDeleted = false;

    public enum Type {
        TEXT, IMAGE, VIDEO, AUDIO
    }
}