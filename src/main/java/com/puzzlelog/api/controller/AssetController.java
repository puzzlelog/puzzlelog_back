package com.puzzlelog.api.controller;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.AuthService;
import com.puzzlelog.api.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/assets")
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
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 자산을 추가할 수 있습니다."));
        }

        try {
            Asset savedAsset = assetService.addAsset(name, type, file);
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
    public ResponseEntity<ApiResponse<Asset>> getAssetById(@PathVariable String id) {
        return assetService.getAssetById(id)
                .map(asset -> ResponseEntity.ok(ApiResponse.success(asset, "자산 조회 성공")))
                .orElse(ResponseEntity.status(404).body(ApiResponse.fail("자산을 찾을 수 없습니다.")));
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
