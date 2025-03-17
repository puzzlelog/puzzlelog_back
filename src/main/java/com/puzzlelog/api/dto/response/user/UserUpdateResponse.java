package com.puzzlelog.api.dto.response.user;

import java.util.Map;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {
    private String userId;
    private Map<String, UpdateField> updatedFields;
    private String mediaId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}
