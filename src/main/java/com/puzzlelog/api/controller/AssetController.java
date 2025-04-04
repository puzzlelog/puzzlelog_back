package com.puzzlelog.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            //4월 1일 추가
            @RequestPart(value = "locked", required = false) Boolean locked,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 자산을 추가할 수 있습니다."));
        }

        try {
            Asset savedAsset = assetService.addAsset(name, type, tag, locked, file);
            return ResponseEntity.ok(ApiResponse.success(savedAsset, "자산이 추가되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    // 삭제되지 않은 모든 asset 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset>>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(ApiResponse.success(assets, "자산 목록 조회 성공"));
    }

    // 특정 타입의 asset 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Asset>>> getAssetsByType(@PathVariable String type) {
        List<Asset> assets = assetService.getAssetsByType(type);
        return ResponseEntity.ok(ApiResponse.success(assets, "특정 타입 자산 조회 성공"));
    }

    // 특정 ID의 asset 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetResponse>> getAsset(@PathVariable String id) {
        AssetResponse assetResponse = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponse.success(assetResponse, "자산 조회 성공"));
    }

    // asset 논리적 삭제 (관리자만 가능)
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
    
    //assest 잠금
    @PatchMapping("/lock-by-tag")
    public ResponseEntity<ApiResponse<Void>> updateLockByTag(
            @RequestHeader("userId") String userId,
            @RequestBody Map<String, Object> request) {

        // 1. 관리자 권한 확인
        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 수정할 수 있습니다."));
        }

        // 2. tag와 locked 값 추출
        String tag = (String) request.get("tag");
        Boolean locked = (Boolean) request.get("locked");

        if (tag == null || locked == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("tag와 locked는 필수입니다."));
        }

        // 3. 서비스 호출
        assetService.updateLockedByTag(tag, locked);

        return ResponseEntity.ok(ApiResponse.successMessage("카테고리 잠금 상태가 변경되었습니다."));
    }
    
    // 사용자별 스티커 조회
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<List<Asset>>> getUserAssetByType(
    		@PathVariable String userId,
    		@PathVariable String type) {
    	List<Asset> assets = assetService.getUserAssetsByType(userId, type);
    	return ResponseEntity.ok(ApiResponse.success(assets, "사용자별 스티커 목록 조회 성공"));
    }
    
    @GetMapping("/user/{userId}/sticker/{stickerId}")
    public ResponseEntity<ApiResponse<Boolean>> canUseSticker(
    		@PathVariable String userId, @PathVariable String stickerId) {
    	boolean canUse = assetService.canUserUseSticker(userId, stickerId);
    	return ResponseEntity.ok(ApiResponse.success(canUse, "스티커 사용 가능 여부 조회 성공"));
    }
}  