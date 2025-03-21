package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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
public class PieceRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotNull(message = "타입은 필수입니다.")
    private String type; // TEXT, IMAGE, VIDEO, AUDIO

    private String content;  // Type이 TEXT일 때 필수
    
    private List<String> tags;

    private GeoJsonPoint location;

    @Builder.Default
    private Boolean isPrivate = false;
}
