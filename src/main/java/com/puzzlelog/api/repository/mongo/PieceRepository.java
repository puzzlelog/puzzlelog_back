package com.puzzlelog.api.repository.mongo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.puzzlelog.api.dao.document.Piece;

@Repository
public interface PieceRepository extends MongoRepository<Piece, String> {
    
    // 특정 사용자 조회
    List<Piece> findByUserIdAndIsDeletedFalse(Integer userId);

    // 모든 활성 조각 조회
    List<Piece> findByIsDeletedFalse();

    // 페이징 처리 가능
    Page<Piece> findByUserIdAndIsDeletedFalse(Integer userId, Pageable pageable);
    Page<Piece> findByIsDeletedFalse(Pageable pageable);
}