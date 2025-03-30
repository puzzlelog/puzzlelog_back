package com.puzzlelog.api.dto.response.piece;

import lombok.*;

import java.util.Map;

/**
 * 조각 수정 응답 DTO입니다.
 * 어떤 필드가 어떻게 수정되었는지를 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceUpdateResponse {

    /** 수정된 조각의 ID */
    private String id;

    /** 조각 작성자 ID */
    private String userId;

    /**
     * 수정된 필드 목록
     * key = 필드명 (예: text, tags, location 등)
     * value = 변경 전/후 값
     */
    private Map<String, UpdateField> updatedFields;

    /**
     * 필드 수정 전/후 값을 담는 객체입니다.
     * 예: {"text": {"before": "Hello", "after": "Hello world"}}
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpdateField {

        /** 수정 전 값 */
        private Object before;

        /** 수정 후 값 */
        private Object after;
    }
}
