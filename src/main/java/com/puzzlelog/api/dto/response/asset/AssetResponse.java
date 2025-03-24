package com.puzzlelog.api.dto.response.asset;

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

    public static AssetResponse from(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .type(asset.getType())
                .imageUrl(asset.getImageUrl())
                .build();
    }
}
