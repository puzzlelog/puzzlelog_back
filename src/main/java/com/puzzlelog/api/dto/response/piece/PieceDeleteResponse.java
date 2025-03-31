package com.puzzlelog.api.dto.response.piece;

import lombok.*;

/**
 * 조각 삭제 응답 DTO입니다.
 * 삭제된 조각의 ID와 사용자 ID를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceDeleteResponse {

    /** 삭제된 조각의 ID (MongoDB _id) */
    private String id;

    /** 해당 조각을 소유한 사용자 ID */
    private String userId;

    /**
     * 조각 ID와 사용자 ID로부터 응답 객체를 생성합니다.
     *
     * @param id 조각 ID
     * @param userId 사용자 ID
     * @return PieceDeleteResponse 인스턴스
     */
    public static PieceDeleteResponse from(String id, String userId) {
        return PieceDeleteResponse.builder()
                .id(id)
                .userId(userId)
                .build();
    }
}