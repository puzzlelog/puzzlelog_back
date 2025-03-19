package com.puzzlelog.api.repository.mongo;

import com.puzzlelog.api.dao.document.Sticker;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface StickerRepository extends MongoRepository<Sticker, String> {

    // 삭제되지 않은 모든 스티커 조회
    List<Sticker> findByIsDeletedFalse();

    // 특정 타입의 삭제되지 않은 스티커 조회
    List<Sticker> findByTypeAndIsDeletedFalse(String type);

    // 특정 ID의 삭제되지 않은 스티커 조회 (단일 조회)
    Optional<Sticker> findByIdAndIsDeletedFalse(String id);
}
