package com.puzzlelog.api.service;

import java.io.IOException;
import java.time.Instant;
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
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.piece.PieceRequest;
import com.puzzlelog.api.dto.request.piece.PieceSearchRequest;
import com.puzzlelog.api.dto.request.piece.PieceUpdateRequest;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.dto.response.piece.PagedPieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceDeleteResponse;
import com.puzzlelog.api.dto.response.piece.PieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceUpdateResponse;
import com.puzzlelog.api.repository.listsearch.PieceListSearch;
import com.puzzlelog.api.repository.mongo.PieceRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PieceService {

	private static final Logger logger = LoggerFactory.getLogger(PieceService.class);
	
    private final PieceRepository pieceRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;
    private final PieceListSearch pieceListSearch;

    // 조각 추가 메서드 (완전한 형태)
    public PieceResponse addPiece(PieceRequest request, MultipartFile file) {
        
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new RuntimeException("사용자 ID는 필수입니다.");
        }
        
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new RuntimeException("타입은 필수입니다.");
        }
        
        if ("TEXT".equals(request.getType()) && (request.getContent() == null || request.getContent().trim().isEmpty())) {
            throw new RuntimeException("텍스트 타입의 경우 내용(content)은 필수입니다.");
        }

        // 사용자 존재 여부 및 상태 체크 (추가된 부분)
        User user = userRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if ("BANNED".equals(user.getStatus())) {
            throw new RuntimeException("차단된 사용자는 조각을 추가할 수 없습니다.");
        }
        
        if ("DELETED".equals(user.getStatus())) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        if (!"TEXT".equals(request.getType()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("TEXT 이외의 타입은 파일이 반드시 포함되어야 합니다.");
        }

        String mediaId = null;
        String publicId = null;

        if (file != null) {
            try {
                logger.info("파일 업로드 시작: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadToCloud(file);

                mediaId = uploadResult.getUrl();
                publicId = uploadResult.getPublicId();

                logger.info("Cloudinary 업로드 성공: {}, publicId: {}", mediaId, publicId);
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
                .publicId(publicId)
                .isDeleted(false)  // 명시적으로 false 추가
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
        Criteria criteria = pieceListSearch.buildSearch(request);
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

        if (request.getType() != null && !request.getType().equals(piece.getType())) {
            updatedFields.put("type", new PieceUpdateResponse.UpdateField(piece.getType(), request.getType()));

            handleTypeChange(piece, request, file, updatedFields);
            piece.setType(request.getType());
        }

        if (file != null && !"TEXT".equals(piece.getType())) {
            replaceExistingFile(piece, file, updatedFields);
        }

        updateAdditionalFields(piece, request, updatedFields);

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

    private void handleTypeChange(Piece piece, PieceUpdateRequest request, MultipartFile file,
                                  Map<String, PieceUpdateResponse.UpdateField> updatedFields) {
        deleteExistingMediaIfExists(piece);

        if ("TEXT".equals(request.getType())) {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                throw new RuntimeException("TEXT 타입으로 변경 시 내용은 필수입니다.");
            }
            updatedFields.put("content", new PieceUpdateResponse.UpdateField(piece.getContent(), request.getContent()));
            piece.setContent(request.getContent());
            piece.setMediaId(null);
        } else {
            if (file == null) throw new RuntimeException("파일이 필수입니다.");

            CloudinaryUploadResponse response = uploadNewFile(file);
            String newMediaId = response.getUrl(); // URL을 mediaId로 사용
            
            updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), newMediaId));
            piece.setMediaId(newMediaId);
            piece.setContent(null);
        }
    }

    private void replaceExistingFile(Piece piece, MultipartFile file, 
                                     Map<String, PieceUpdateResponse.UpdateField> updatedFields) {
        deleteExistingMediaIfExists(piece);

        CloudinaryUploadResponse response = uploadNewFile(file);
        String newMediaId = response.getUrl(); // URL을 mediaId로 사용
        
        updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), newMediaId));
        piece.setMediaId(newMediaId);
    }

    private void updateAdditionalFields(Piece piece, PieceUpdateRequest request,
            Map<String, PieceUpdateResponse.UpdateField> updatedFields) {
    	
		if (request.getContent() != null && "TEXT".equals(piece.getType())
			&& !request.getContent().equals(piece.getContent())) {
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
    }

    private void deleteExistingMediaIfExists(Piece piece) {
        if (piece.getMediaId() != null && piece.getPublicId() != null) {
            String type = piece.getType();
            String resourceType = ("VIDEO".equals(type) || "AUDIO".equals(type)) ? "video" : "image";
            boolean deleted = cloudinaryService.deleteFromCloud(piece.getPublicId(), resourceType);
            if (!deleted) logger.warn("기존 파일이 이미 삭제되었거나 존재하지 않음. publicId: {}", piece.getPublicId());
        }
    }
    
    private CloudinaryUploadResponse uploadNewFile(MultipartFile file) {
        try {
            return cloudinaryService.uploadToCloud(file);
        } catch (IOException e) {
            logger.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
    
    // 조각 삭제
    @Transactional
    public PieceDeleteResponse deletePiece(String pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 조각입니다."));

        // 이미 삭제된 경우 처리
        if (piece.getIsDeleted()) {
            throw new RuntimeException("존재하지 않는 조각입니다.");
        }

        piece.setIsDeleted(true);  // 삭제 처리
        pieceRepository.save(piece);

        return PieceDeleteResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .build();
    }

}
