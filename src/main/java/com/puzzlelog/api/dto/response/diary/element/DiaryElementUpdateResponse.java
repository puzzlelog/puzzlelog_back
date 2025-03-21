package com.puzzlelog.api.dto.response.diary.element;

import java.util.Map;

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
public class DiaryElementUpdateResponse {
    private String elementId;
    private Map<String, UpdateField> updatedFields;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}