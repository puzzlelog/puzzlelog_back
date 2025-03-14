package com.puzzlelog.api.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateResponse {
    private String id;          // 조각 ID
    private Integer userId;     // 사용자 ID
    private Map<String, UpdateField> updatedFields;  // 수정된 필드 정보

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}
