package com.puzzlelog.api.dto.response.diary.meta;

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
public class DiaryMetaUpdateResponse {
    private String diaryId;
    private Map<String, UpdateField> updatedFields;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}