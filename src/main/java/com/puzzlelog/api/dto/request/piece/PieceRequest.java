package com.puzzlelog.api.dto.request.piece;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.document.Piece.Type;

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
    private Integer userId;

    @NotNull(message = "타입은 필수입니다.")
    private Type type;

    private String content;  // Type이 TEXT일 때 필수
    
    private List<String> tags;

    private GeoJsonPoint location;

    @Builder.Default
    private Boolean isPrivate = false;
    
    // MultipartFile mediaFile은 여기서 제외하고, 컨트롤러 메서드에서 직접 받도록 함.
}