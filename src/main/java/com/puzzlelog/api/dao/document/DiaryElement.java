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
@Document(collection = "diary_elements")
public class DiaryElement {

	@Id
	private String id;

    private String diaryId;
    private String elementType; // TEXT, IMAGE, AUDIO, VIDEO, STICKER, DRAWING
    private String contentId;
    private String drawingData;

    @Builder.Default
    private List<Double> position = List.of(0.0, 0.0);
    @Builder.Default
    private Double scale = 1.0;
    @Builder.Default
    private Double rotation = 0.0;

    private ElementDecoration decoration;

    @CreatedDate
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private Boolean isDeleted = false; // 논리적 삭제 여부
}
