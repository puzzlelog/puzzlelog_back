package com.puzzlelog.api.dto.response.piece;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceDeleteResponse {
    private String id;      
    private String userId;

    public static PieceDeleteResponse from(String id, String userId) {
        return PieceDeleteResponse.builder()
                .id(id)
                .userId(userId)
                .build();
    }
}