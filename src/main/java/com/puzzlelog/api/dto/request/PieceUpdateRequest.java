package com.puzzlelog.api.dto.request;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.puzzlelog.api.dao.document.Piece.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PieceUpdateRequest {

    private Type type;  
    private String content;
    private List<String> tags;
    private GeoJsonPoint location;
    private String mediaId;
    private Boolean isPrivate;
}
