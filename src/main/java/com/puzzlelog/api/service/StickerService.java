package com.puzzlelog.api.service;

import com.puzzlelog.api.dao.document.Sticker;
import com.puzzlelog.api.repository.mongo.StickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StickerService {
    @Autowired
    private StickerRepository stickerRepository;

    // 스티커 추가
    public Sticker addSticker(Sticker sticker) {
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

    // 스티커 논리적 삭제 (isDeleted = true로 변경)
    public boolean deleteSticker(String id) {
        Optional<Sticker> stickerOptional = stickerRepository.findById(id);
        if (stickerOptional.isPresent()) {
            Sticker sticker = stickerOptional.get();
            sticker.setDeleted(true); // 논리적 삭제 처리
            stickerRepository.save(sticker);
            return true;
        }
        return false;
    }
}
