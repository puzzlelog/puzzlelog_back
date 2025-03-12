package com.puzzlelog.api.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class UserUpdateResponse {
    private String userId;
    private Map<String, Object> updatedFields;
}