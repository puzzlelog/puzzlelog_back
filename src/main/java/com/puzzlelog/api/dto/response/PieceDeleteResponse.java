package com.puzzlelog.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceDeleteResponse {
    private String id;          // 조각 ID
    private Integer userId;     // 사용자 ID

    public static PieceDeleteResponse from(String id, Integer userId, Boolean isDeleted) {
        return PieceDeleteResponse.builder()
                .id(id)
                .userId(userId)
                .build();
    }
}