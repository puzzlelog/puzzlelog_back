package com.puzzlelog.api.repository.mongo;

import com.puzzlelog.api.dao.document.PieceDeleteHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 조각 이력 저장소 (삭제, 복원, 수정, 조회 등 기록)
 */
@Repository
public interface PieceHistoryRepository extends MongoRepository<PieceDeleteHistory, String> {
}
