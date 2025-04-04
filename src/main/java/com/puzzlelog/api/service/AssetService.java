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
    
    @Autowired
    private UserService userService;

    // 에셋 추가 (multipart 지원)
    public Asset addAsset(String name, String type, String tag, Boolean locked, MultipartFile file) {
        if (name.isBlank() || type.isBlank()) {
            throw new IllegalArgumentException("에셋 이름과 타입은 필수입니다.");
        }

        String mediaId = null;
        String publicId = null;
        
        if (file != null && !file.isEmpty()) {
            try {
                // Cloudinary에 업로드하고 URL 및 publicId 받기
                CloudinaryUploadResponse uploadResponse = cloudinaryService.uploadToCloud(file);
                mediaId = uploadResponse.getUrl();
                publicId = uploadResponse.getPublicId(); // publicId도 저장
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        Asset asset = Asset.builder()
                .id(UUID.randomUUID().toString()) // MongoDB용 UUID 생성
                .name(name)
                .type(type)
                .mediaId(mediaId)
                .publicId(publicId)
                .tags(tag != null && !tag.isBlank() ? List.of(tag) : List.of()) // tag를 List로 변환
                .locked(locked != null ? locked : true) 
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
    
    // 스티커 잠금
    public void updateLockedByTag(String tag, boolean locked) {
        List<Asset> assets = assetRepository.findByTypeAndDeletedFalse(tag);

        for (Asset asset : assets) {
            asset.setLocked(locked);
        }

        assetRepository.saveAll(assets);
    }

    public void unlockAllPaidStickers() {
    	List<Asset> assets = assetRepository.findByTypeAndDeletedFalse("STICKER");
    	for (Asset asset : assets) {
    		asset.setLocked(false);
    	}
    	assetRepository.saveAll(assets);
    }
    
    public void lockAllPaidStickers() {
    	List<Asset> assets = assetRepository.findByTypeAndDeletedFalse("STICKER");
    	for (Asset asset : assets) {
    		asset.setLocked(true);
    	}
    	assetRepository.saveAll(assets);
    }
    
    // 사용자별 스티커 목록 조회
    public List<Asset> getUserAssets(String userId) {
    	boolean isSubscribed = userService.isUserSubscribed(userId);
    	List<Asset> assets = assetRepository.findByDeletedFalse();
    	
    	// 사용자 구독 상태에 따라 잠금 상태 설정
    	for (Asset asset : assets) {
    		if ("STICKER".equals(asset.getType())) {
    			asset.setLocked(!isSubscribed); // 구독 상태에 따라 잠금 여부 결정
    		}
    	}
    	return assets;
    }
    
    // 스티커 잠금 상태를 결정하는 메서드
    public List<Asset> getUserAssetsByType(String userId, String type) {
    	boolean isSubscribed = userService.isUserSubscribed(userId);
    	List<Asset> assets = assetRepository.findByTypeAndDeletedFalse(type);
    	
    	for (Asset asset : assets) {
    		if ("STICKER".equals(asset.getType())) {
    			// 관리자 잠금 상태가 true라면 무조건 잠김
    			if (asset.getLocked() != null && asset.getLocked()) {
    				asset.setLocked(true);
    			} else {
    				// 관리자 잠금 상태가 false이거나 null이면 구독 상태에 따라 잠금 여부 결정
    				asset.setLocked(!isSubscribed);
    			}
    		}
    	}
    	return assets;
    }
    
    public boolean canUserUseSticker(String userId, String stickerId) {
    	Asset asset = assetRepository.findById(stickerId).orElse(null);
    	if (asset == null || asset.isDeleted()) return false;
    	
    	// 관리자가 잠근 스티커일 경우
    	if (asset.getLocked()) {
    		// 사용자가 구독 상태인지 확인
    		boolean isSubscribed = userService.isUserSubscribed(userId);
    		return isSubscribed;
    	}
    	
    	// 잠겨 있지 않다면 무조건 사용 가능
    	return true;
    }
}
