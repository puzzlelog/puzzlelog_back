package com.puzzlelog.api.repository.mongo;

import com.puzzlelog.api.dao.document.Sticker;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StickerRepository extends MongoRepository<Sticker, String> {
    List<Sticker> findByIsDeletedFalse();  // 삭제되지 않은 스티커만 조회
    List<Sticker> findByTypeAndIsDeletedFalse(String type);  // 특정 타입의 삭제되지 않은 스티커 조회
}
