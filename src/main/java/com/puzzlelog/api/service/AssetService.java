package com.puzzlelog.api.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.document.Asset;
import com.puzzlelog.api.dto.response.asset.AssetResponse;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.repository.mongo.AssetRepository;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CloudinaryService cloudinaryService; // ✅ Cloudinary 업로드 서비스 사용

    // 에셋 추가 (multipart 지원)
    public Asset addAsset(String name, String type, MultipartFile file) {
        if (name.isBlank() || type.isBlank()) {
            throw new IllegalArgumentException("에셋 이름과 타입은 필수입니다.");
        }

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                // ✅ Cloudinary에 업로드하고 URL 받기
                CloudinaryUploadResponse uploadResponse = cloudinaryService.uploadToCloud(file);
                imageUrl = uploadResponse.getUrl();
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        Asset asset = Asset.builder()
                .id(UUID.randomUUID().toString()) // MongoDB용 UUID 생성
                .name(name)
                .type(type)
                .imageUrl(imageUrl)
                .deleted(false)
                .build();

        return assetRepository.save(asset);
    }

    // 삭제되지 않은 모든 자산 조회
    public List<Asset> getAllAssets() {
        return assetRepository.findByDeletedFalse();
    }

    // 특정 타입의 삭제되지 않은 자산 조회
    public List<Asset> getAssetsByType(String type) {
        return assetRepository.findByTypeAndDeletedFalse(type);
    }

    // 특정 ID의 삭제되지 않은 자산 조회
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(String id) {
        Asset asset = assetRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("자산을 찾을 수 없습니다."));

        return AssetResponse.from(asset);
    }

    // 에셋 논리적 삭제 (isDeleted = true로 변경)
    public boolean deleteAsset(String id) {
        Optional<Asset> assetOptional = assetRepository.findById(id);
        if (assetOptional.isPresent()) {
            Asset asset = assetOptional.get();
            asset.setDeleted(true);
            assetRepository.save(asset);
            return true;
        }
        return false;
    }
}
