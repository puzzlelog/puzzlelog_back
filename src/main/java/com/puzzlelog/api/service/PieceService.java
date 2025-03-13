package com.puzzlelog.api.service;

import java.time.Instant;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Piece;
import com.puzzlelog.api.dto.request.PieceRequest;
import com.puzzlelog.api.dto.response.PagedPieceResponse;
import com.puzzlelog.api.dto.response.PieceResponse;
import com.puzzlelog.api.repository.mongo.PieceRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class PieceService {

    private final PieceRepository pieceRepository;
    private final UserRepository userRepository;

    public PieceService(PieceRepository pieceRepository, UserRepository userRepository) {
        this.pieceRepository = pieceRepository;
        this.userRepository = userRepository;
    }

    // 조각 추가 메서드
    public PieceResponse addPiece(PieceRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("사용자 ID는 필수입니다.");
        }
        if (request.getType() == null) {
            throw new RuntimeException("타입은 필수입니다.");
        }
        if (request.getType() == Piece.Type.TEXT && (request.getContent() == null || request.getContent().trim().isEmpty())) {
            throw new RuntimeException("텍스트 타입의 경우 내용(content)은 필수입니다.");
        }
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        Piece piece = Piece.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .content(request.getContent())
                .location(request.getLocation())
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .createdAt(Instant.now())
                .build();

        Piece savedPiece = pieceRepository.save(piece);

        return PieceResponse.from(savedPiece);
    }
    
    // 단일 조회 메서드
    @Transactional(readOnly = true)
    public PieceResponse getPiece(String pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        if (piece.getIsDeleted()) {
            throw new RuntimeException("조각을 찾을 수 없습니다.");
        }

        return PieceResponse.from(piece);
    }

    // 목록 조회 메서드 (페이징 포함)
    @Transactional(readOnly = true)
    public PagedPieceResponse getPieces(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (userId != null && !userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        Page<Piece> pieces;

        if (userId != null) {
            pieces = pieceRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        } else {
            pieces = pieceRepository.findByIsDeletedFalse(pageable);
        }
        
        if (pieces.isEmpty()) {
            throw new RuntimeException("조회된 조각이 없습니다.");
        }

        // Entity → DTO 변환 후 페이징 처리
        Page<PieceResponse> pieceResponses = pieces.map(PieceResponse::from);

        return PagedPieceResponse.from(pieceResponses);
    }
}
