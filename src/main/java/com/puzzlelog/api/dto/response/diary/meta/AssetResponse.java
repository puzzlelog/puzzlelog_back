package com.puzzlelog.api.dto.response.diary.meta;

import com.puzzlelog.api.dao.document.Asset;

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
public class AssetResponse {
    private String id;
    private String name;
    private String type;
    private String imageUrl;

    public static AssetResponse from(Asset sticker) {
    	if (sticker == null || sticker.isDeleted()) {
    	    return null;
    	}
        return AssetResponse.builder()
            .id(sticker.getId())
            .name(sticker.getName())
            .type(sticker.getType())
            .imageUrl(sticker.getImageUrl())
            .build();
    }
}