package com.puzzlelog.api.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;  // Type이 TEXT일 때 필수
    
    private List<String> tags; // 태그 선택적

    // 위치정보는 선택적
    private GeoJsonPoint location;

    // 기본값이 있으므로 명시적으로 입력하지 않아도 된다.
    @Builder.Default
    private Boolean isPrivate = false;
}
