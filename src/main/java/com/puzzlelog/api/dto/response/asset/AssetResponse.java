package com.puzzlelog.api.dto.response.asset;

import com.puzzlelog.api.dao.document.Asset;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {
    private String id;
    private String name;
    private String type;
    private String mediaId;
    private String publicId;
    private List<String> tags;
    private Boolean locked;

    public static AssetResponse from(Asset asset) {
        if (asset == null) {
            return null;
        }

        return AssetResponse.builder()
            .id(asset.getId())
            .name(asset.getName())
            .type(asset.getType())
            .mediaId(asset.getMediaId())
            .publicId(asset.getPublicId())
            .tags(asset.getTags())
            .locked(asset.getLocked())
            .build();
    }
}
