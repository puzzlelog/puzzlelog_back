package com.puzzlelog.api.dto.response.piece;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CloudinaryUploadResponse {
    private final String url;
    private final String publicId;
}