package com.puzzlelog.api.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Piece;
import com.puzzlelog.api.dto.request.PieceRequest;
import com.puzzlelog.api.dto.request.PieceUpdateRequest;
import com.puzzlelog.api.dto.response.PagedPieceResponse;
import com.puzzlelog.api.dto.response.PieceDeleteResponse;
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
                .tags(request.getTags())
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
    
    // 조각 변경
    @Transactional
    public PieceResponse updatePiece(String pieceId, PieceUpdateRequest request) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        boolean updated = false;

        if (request.getType() != null && request.getType() != piece.getType()) {
            piece.setType(request.getType());
            updated = true;
        }
        if (request.getContent() != null && !request.getContent().equals(piece.getContent())) {
            piece.setContent(request.getContent());
            updated = true;
        }
        if (request.getTags() != null && !request.getTags().equals(piece.getTags())) {
            piece.setTags(request.getTags());
            updated = true;
        }
        if (request.getLocation() != null && !request.getLocation().equals(piece.getLocation())) {
            piece.setLocation(request.getLocation());
            updated = true;
        }
        if (request.getMediaId() != null && !request.getMediaId().equals(piece.getMediaId())) {
            piece.setMediaId(request.getMediaId());
            updated = true;
        }
        if (request.getIsPrivate() != null && !request.getIsPrivate().equals(piece.getIsPrivate())) {
            piece.setIsPrivate(request.getIsPrivate());
            updated = true;
        }

        if (updated) {
            pieceRepository.save(piece);
        }

        return PieceResponse.from(piece);
    }
    
    // 조각 삭제
    @Transactional
    public PieceDeleteResponse deletePiece(String pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        // 이미 삭제된 경우 처리
        if (piece.getIsDeleted()) {
            throw new RuntimeException("이미 삭제된 조각입니다.");
        }

        piece.setIsDeleted(true);  // 삭제 처리
        pieceRepository.save(piece);

        return PieceDeleteResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .build();
    }

}
