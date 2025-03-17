package com.puzzlelog.api.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dao.document.Piece;
import com.puzzlelog.api.dto.request.piece.PieceRequest;
import com.puzzlelog.api.dto.request.piece.PieceSearchRequest;
import com.puzzlelog.api.dto.request.piece.PieceUpdateRequest;
import com.puzzlelog.api.dto.response.piece.PagedPieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceDeleteResponse;
import com.puzzlelog.api.dto.response.piece.PieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceUpdateResponse;
import com.puzzlelog.api.repository.mongo.PieceRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class PieceService {

	private static final Logger logger = LoggerFactory.getLogger(PieceService.class);
	
    private final PieceRepository pieceRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;

    public PieceService(PieceRepository pieceRepository, UserRepository userRepository, 
    		MongoTemplate mongoTemplate, CloudinaryService cloudinaryService) {
        this.pieceRepository = pieceRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.cloudinaryService = cloudinaryService;
    }

    // 조각 추가 메서드
    public PieceResponse addPiece(PieceRequest request, MultipartFile file) {
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

        if (request.getType() != Piece.Type.TEXT && file == null) {
            throw new RuntimeException("TEXT 이외의 타입은 파일이 반드시 포함되어야 합니다.");
        }

        String mediaId = null;
        if (file != null) {
            try {
                logger.info("파일 업로드 시작: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
                mediaId = cloudinaryService.uploadToCloud(file);
                logger.info("Cloudinary 업로드 성공: {}", mediaId);
            } catch (Exception e) {
                logger.error("Cloudinary 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
        }

        Piece piece = Piece.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .content(request.getContent())
                .tags(request.getTags())
                .location(request.getLocation())
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .mediaId(mediaId)
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
    
    // 아무 것도 입력하지 않았을 때
    @Transactional(readOnly = true)
    public PagedPieceResponse getPieces(int page, int size) {
        Query query = new Query(Criteria.where("isDeleted").is(false))
            .with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Piece> pieces = mongoTemplate.find(query, Piece.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Piece.class);

        return PagedPieceResponse.of(pieces, page, size, total);
    }

    // 목록 조회 메서드 (페이징 포함)
    @Transactional(readOnly = true)
    public PagedPieceResponse searchPieces(PieceSearchRequest request, int page, int size) {
        request.applyDateFilters(); // 추가된 메서드 호출

        Criteria criteria = new Criteria();

        if (request.getUserId() != null) {
            criteria.and("userId").is(request.getUserId());
        }
        if (request.getType() != null) {
            criteria.and("type").is(request.getType());
        }
        if (request.getContent() != null) {
            criteria.and("content").regex(".*" + request.getContent() + ".*", "i");
        }
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            criteria.and("tags").in(request.getTags());
        }
        if (request.getIsPrivate() != null) {
            criteria.and("isPrivate").is(request.getIsPrivate());
        }
        if (request.getIsDeleted() != null) {
            criteria.and("isDeleted").is(request.getIsDeleted());
        }
        if (request.getCreatedAtFrom() != null || request.getCreatedAtTo() != null) {
            LocalDate fromDate = request.getCreatedAtFrom() != null
                    ? request.getCreatedAtFrom() : LocalDate.of(1970, 1, 1);
            LocalDate toDate = request.getCreatedAtTo() != null
                    ? request.getCreatedAtTo() : LocalDate.now();

            Instant fromInstant = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toInstant = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        }

        Query query = new Query(criteria)
            .with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Piece> pieces = mongoTemplate.find(query, Piece.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Piece.class);

        return PagedPieceResponse.of(pieces, page, size, total);
    }
    
    // 조각 변경
    @Transactional
    public PieceUpdateResponse updatePiece(String pieceId, PieceUpdateRequest request, MultipartFile file) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        Map<String, PieceUpdateResponse.UpdateField> updatedFields = new LinkedHashMap<>();

        // 타입 변경 체크
        if (request.getType() != null && request.getType() != piece.getType()) {
            updatedFields.put("type", new PieceUpdateResponse.UpdateField(piece.getType().name(), request.getType().name()));

            // 타입이 TEXT로 변경될 경우
            if (request.getType() == Piece.Type.TEXT) {
                if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                    throw new RuntimeException("TEXT 타입으로 변경 시 내용은 필수입니다.");
                }

                // 기존 업로드 파일 삭제
                if (piece.getMediaId() != null) {
                    try {
                        cloudinaryService.deleteFromCloud(piece.getMediaId());
                    } catch (Exception e) {
                        logger.error("Cloudinary 파일 삭제 실패: {}", e.getMessage(), e);
                        throw new RuntimeException("기존 파일 삭제 실패: " + e.getMessage(), e);
                    }
                    updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), null));
                    piece.setMediaId(null);
                }

                // content 업데이트
                updatedFields.put("content", new PieceUpdateResponse.UpdateField(piece.getContent(), request.getContent()));
                piece.setContent(request.getContent());

            } else { // 타입이 TEXT → IMAGE, VIDEO, AUDIO로 변경될 경우
                if (file == null) {
                    throw new RuntimeException("파일이 필수입니다.");
                }

                // 파일 업로드
                String newMediaId;
                try {
                    newMediaId = cloudinaryService.uploadToCloud(file);
                } catch (IOException e) {
                    logger.error("Cloudinary 파일 업로드 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
                }
                updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), newMediaId));

                // 기존 업로드 파일 삭제
                if (piece.getMediaId() != null) {
                    try {
                        cloudinaryService.deleteFromCloud(piece.getMediaId());
                    } catch (Exception e) {
                        logger.error("Cloudinary 파일 삭제 실패: {}", e.getMessage(), e);
                        throw new RuntimeException("기존 파일 삭제 실패: " + e.getMessage(), e);
                    }
                }

                piece.setMediaId(newMediaId);
                piece.setContent(null);
            }

            piece.setType(request.getType());
        }

        // 같은 타입 내에서 파일 변경 체크 (IMAGE → 다른 IMAGE 등)
        if (file != null && piece.getType() != Piece.Type.TEXT) {
            String newMediaId;
            try {
                newMediaId = cloudinaryService.uploadToCloud(file);
            } catch (IOException e) {
                logger.error("Cloudinary 파일 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
            }
            updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), newMediaId));

            // 기존 파일 삭제
            if (piece.getMediaId() != null) {
                try {
                    cloudinaryService.deleteFromCloud(piece.getMediaId());
                } catch (Exception e) {
                    logger.error("Cloudinary 파일 삭제 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("기존 파일 삭제 실패: " + e.getMessage(), e);
                }
            }

            piece.setMediaId(newMediaId);
        }

        // 기타 필드 업데이트 체크
        if (request.getContent() != null && piece.getType() == Piece.Type.TEXT && !request.getContent().equals(piece.getContent())) {
            updatedFields.put("content", new PieceUpdateResponse.UpdateField(piece.getContent(), request.getContent()));
            piece.setContent(request.getContent());
        }

        if (request.getTags() != null && !request.getTags().equals(piece.getTags())) {
            updatedFields.put("tags", new PieceUpdateResponse.UpdateField(piece.getTags(), request.getTags()));
            piece.setTags(request.getTags());
        }

        if (request.getLocation() != null && !request.getLocation().equals(piece.getLocation())) {
            updatedFields.put("location", new PieceUpdateResponse.UpdateField(piece.getLocation(), request.getLocation()));
            piece.setLocation(request.getLocation());
        }

        if (request.getIsPrivate() != null && !request.getIsPrivate().equals(piece.getIsPrivate())) {
            updatedFields.put("isPrivate", new PieceUpdateResponse.UpdateField(piece.getIsPrivate(), request.getIsPrivate()));
            piece.setIsPrivate(request.getIsPrivate());
        }

        if (updatedFields.isEmpty()) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }

        pieceRepository.save(piece);

        return PieceUpdateResponse.builder()
            .id(piece.getId())
            .userId(piece.getUserId())
            .updatedFields(updatedFields)
            .build();
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
