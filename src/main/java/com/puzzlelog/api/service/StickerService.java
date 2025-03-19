package com.puzzlelog.api.service;

import com.puzzlelog.api.dao.document.Sticker;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.repository.mongo.StickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StickerService {

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private CloudinaryService cloudinaryService; // ✅ Cloudinary 업로드 서비스 사용

    // 스티커 추가 (multipart 지원)
    public Sticker addSticker(String name, String type, MultipartFile file) {
        if (name.isBlank() || type.isBlank()) {
            throw new IllegalArgumentException("스티커 이름과 타입은 필수입니다.");
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

        Sticker sticker = Sticker.builder()
                .id(UUID.randomUUID().toString()) // MongoDB용 UUID 생성
                .name(name)
                .type(type)
                .imageUrl(imageUrl)
                .isDeleted(false)
                .build();

        return stickerRepository.save(sticker);
    }

    // 삭제되지 않은 모든 스티커 조회
    public List<Sticker> getAllStickers() {
        return stickerRepository.findByIsDeletedFalse();
    }

    // 특정 타입의 삭제되지 않은 스티커 조회
    public List<Sticker> getStickersByType(String type) {
        return stickerRepository.findByTypeAndIsDeletedFalse(type);
    }

    // 특정 ID의 삭제되지 않은 스티커 조회
    public Optional<Sticker> getStickerById(String id) {
        return stickerRepository.findByIdAndIsDeletedFalse(id);
    }

    // 스티커 논리적 삭제 (isDeleted = true로 변경)
    public boolean deleteSticker(String id) {
        Optional<Sticker> stickerOptional = stickerRepository.findById(id);
        if (stickerOptional.isPresent()) {
            Sticker sticker = stickerOptional.get();
            sticker.setIsDeleted(true);
            stickerRepository.save(sticker);
            return true;
        }
        return false;
    }
}
