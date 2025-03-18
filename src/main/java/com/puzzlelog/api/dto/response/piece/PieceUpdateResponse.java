package com.puzzlelog.api.dto.response.piece;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateResponse {
    private String id;
    private String userId;
    private Map<String, UpdateField> updatedFields;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}