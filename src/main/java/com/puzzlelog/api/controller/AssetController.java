package com.puzzlelog.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dto.response.asset.AssetResponse;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.AssetService;
import com.puzzlelog.api.service.AuthService;

@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AuthService authService;

    // 에셋 추가 (배경, 이모션, 스티커 등)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Asset>> addAsset(
            @RequestHeader("userId") String userId,
            @RequestPart("name") String name,
            @RequestPart("type") String type,
            @RequestPart("tag") String tag,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 자산을 추가할 수 있습니다."));
        }

        try {
            Asset savedAsset = assetService.addAsset(name, type, tag, file);
            return ResponseEntity.ok(ApiResponse.success(savedAsset, "자산이 추가되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    // 삭제되지 않은 모든 자산 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset>>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(ApiResponse.success(assets, "자산 목록 조회 성공"));
    }

    // 특정 타입의 자산 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Asset>>> getAssetsByType(@PathVariable String type) {
        List<Asset> assets = assetService.getAssetsByType(type);
        return ResponseEntity.ok(ApiResponse.success(assets, "특정 타입 자산 조회 성공"));
    }

    // 특정 ID의 자산 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetResponse>> getAsset(@PathVariable String id) {
        AssetResponse assetResponse = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponse.success(assetResponse, "자산 조회 성공"));
    }

    // 자산 논리적 삭제 (관리자만 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(
            @RequestHeader("userId") String userId,
            @PathVariable String id) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 삭제할 수 있습니다."));
        }

        boolean deleted = assetService.deleteAsset(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.successMessage("자산이 삭제되었습니다."));
        } else {
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("자산을 찾을 수 없습니다."));
        }
    }
}
