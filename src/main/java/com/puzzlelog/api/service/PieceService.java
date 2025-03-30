package com.puzzlelog.api.service;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.puzzlelog.api.dto.response.common.Pagination;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;
import com.puzzlelog.api.dto.response.piece.PagedPieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceDeleteResponse;
import com.puzzlelog.api.dto.response.piece.PieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceUpdateResponse;
import com.puzzlelog.api.repository.listsearch.PieceListSearch;
import com.puzzlelog.api.repository.mongo.PieceRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PieceService {

	private static final Logger logger = LoggerFactory.getLogger(PieceService.class);
	
    private final PieceRepository pieceRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;
    private final PieceListSearch pieceListSearch;
    
    // 허용된 타입 상수 정의
    private static final Set<String> ALLOWED_TYPES = Set.of("TEXT", "IMAGE", "VIDEO", "AUDIO");

    /**
     * 조각을 추가합니다.
     * 요청된 조각 정보와 (필요 시) 파일을 바탕으로 MongoDB에 Piece 문서를 생성하고 저장합니다.
     *
     * @param request 조각 생성 요청 데이터 (텍스트, 태그, 위치, 타입 등)
     * @param file 업로드된 파일 (이미지, 오디오, 비디오 등, TEXT 타입은 제외)
     * @return 생성된 조각 정보를 담은 PieceResponse
     */
    public PieceResponse addPiece(PieceRequest request, MultipartFile file) {

        // 사용자 ID 유효성 검사
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new RuntimeException("사용자 ID는 필수입니다.");
        }

        // 타입 유효성 검사
        String type = request.getType();
        if (type == null || type.trim().isEmpty()) {
            throw new RuntimeException("타입은 필수입니다.");
        }

        if (!ALLOWED_TYPES.contains(type)) {
            throw new RuntimeException("유효하지 않은 타입입니다. 허용된 타입: TEXT, IMAGE, VIDEO, AUDIO");
        }

        // TEXT 타입일 경우 텍스트 내용 필수
        if ("TEXT".equals(request.getType()) && (request.getText() == null || request.getText().trim().isEmpty())) {
            throw new RuntimeException("텍스트 타입의 경우 내용(text)은 필수입니다.");
        }

        // 사용자 상태 확인 (BANNED, DELETED 검사)
        User user = userRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if ("BANNED".equals(user.getStatus())) {
            throw new RuntimeException("차단된 사용자는 조각을 추가할 수 없습니다.");
        }

        if ("DELETED".equals(user.getStatus())) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        // TEXT 외 타입은 반드시 파일 포함
        if (!"TEXT".equals(request.getType()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("TEXT 이외의 타입은 파일이 반드시 포함되어야 합니다.");
        }

        String mediaId = null;
        String publicId = null;

        // 파일 업로드 처리 (Cloudinary)
        if (file != null) {
            try {
                logger.info("📤 파일 업로드 시작: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

                CloudinaryUploadResponse uploadResult = cloudinaryService.uploadToCloud(file);
                mediaId = uploadResult.getUrl();
                publicId = uploadResult.getPublicId();

                logger.info("✅ Cloudinary 업로드 성공: {}, publicId: {}", mediaId, publicId);
            } catch (Exception e) {
                logger.error("❌ Cloudinary 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
        }

        // Piece 도큐먼트 생성
        Piece piece = Piece.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .text(request.getText())
            .tags(request.getTags())
            .location(request.getLocation())
            .privatePiece(Boolean.TRUE.equals(request.getPrivatePiece()))
            .mediaId(mediaId)
            .publicId(publicId)
            .deleted(false)
            .createdAt(Instant.now())
            .build();

        Piece savedPiece = pieceRepository.save(piece);

        return PieceResponse.from(savedPiece);
    }

    /**
     * 조각 단일 조회 메서드입니다.
     * MongoDB의 조각 ID를 통해 조각을 조회하며, 삭제된 조각은 조회할 수 없습니다.
     *
     * @param pieceId 조각의 MongoDB ID
     * @return 조회된 조각의 응답 DTO
     * @throws RuntimeException 조각이 존재하지 않거나 삭제된 경우
     */
    @Transactional(readOnly = true)
    public PieceResponse getPiece(String pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        return PieceResponse.from(piece);
    }
    
    /**
     * 조각 목록을 조건에 따라 검색합니다.
     * 요청자 ID에 따라 비공개 조각 필터링 여부가 달라집니다.
     *
     * @param request 검색 조건
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param requesterId 요청자 (JWT의 userId)
     * @return 페이징된 조각 목록 응답
     */
    @Transactional(readOnly = true)
    public PagedPieceResponse searchPieces(PieceSearchRequest request, int page, int size, String requesterId, String role) {
        Criteria criteria;

        // 일반 사용자이고 아무 조건도 없으면 → 기본적으로 본인 조각만 조회
        if (request.hasNoCondition() && !"ROLE_ADMIN".equals(role)) {
            request.setUserId(requesterId);
        }

        // 기본 검색 조건 설정
        if (request.hasNoCondition()) {
            criteria = Criteria.where("deleted").is(false);
        } else {
            criteria = pieceListSearch.buildSearch(request);
            criteria = new Criteria().andOperator(criteria, Criteria.where("deleted").is(false));
        }

        // 일반 사용자 → 내 조각만 or 남의 공개 조각만
        if (!"ROLE_ADMIN".equals(role)) {
            if (request.getUserId() != null && !request.getUserId().equalsIgnoreCase(requesterId)) {
                criteria = new Criteria().andOperator(criteria, Criteria.where("privatePiece").is(false));
            }
        }

        // 쿼리 실행
        Query query = new Query(criteria)
            .with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Piece> pieces = mongoTemplate.find(query, Piece.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Piece.class);

        // 관리자: 비공개는 메타만, 사용자: 본인 거면 full
        List<PieceResponse> pieceResponses = pieces.stream()
            .map(piece -> {
                if ("ROLE_ADMIN".equals(role) && piece.isPrivatePiece()) {
                    return PieceResponse.forAdminPreview(piece);
                } else {
                    return PieceResponse.from(piece);
                }
            })
            .collect(Collectors.toList());

        return PagedPieceResponse.builder()
                .pieces(pieceResponses)
                .pagination(Pagination.of(page, size, total))
                .build();
    }

    
    /**
     * 조각 수정 서비스 로직입니다.
     * 요청자의 권한에 따라 수정 가능한 필드가 다릅니다.
     *
     * - 본인: 전체 필드 수정 가능 (타입, 내용, 미디어, 태그, 위치, 공개 여부)
     * - 관리자: 태그(tags)만 수정 가능
     *
     * @param pieceId 수정할 조각 ID
     * @param request 수정 요청 데이터
     * @param file 업로드할 미디어 파일 (선택)
     * @param requesterId 요청자 ID (JWT에서 추출)
     * @param role 요청자 역할 (예: ROLE_USER, ROLE_ADMIN)
     * @return 수정된 필드 목록을 포함한 응답
     */
    @Transactional
    public PieceUpdateResponse updatePiece(String pieceId, PieceUpdateRequest request, MultipartFile file, String requesterId, String role) {
        Piece piece = pieceRepository.findById(pieceId)
            .orElseThrow(() -> new RuntimeException("조각을 찾을 수 없습니다."));

        boolean isOwner = piece.getUserId().equals(requesterId);
        boolean isAdmin = "ROLE_ADMIN".equals(role);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("조각 수정 권한이 없습니다.");
        }

        // ✅ 관리자 허용 필드 외 요청 여부 검사 (요청 필드 기준)
        if (isAdmin && !isOwner) {
            boolean triedToModifyForbiddenField = request.getType() != null ||
                                                   request.getText() != null ||
                                                   file != null ||
                                                   request.getLocation() != null ||
                                                   request.getPrivatePiece() != null;

            if (triedToModifyForbiddenField) {
                throw new RuntimeException("관리자는 tags만 수정할 수 있습니다.");
            }
        }

        Map<String, PieceUpdateResponse.UpdateField> updatedFields = new LinkedHashMap<>();

        // 본인: 타입 변경 + 파일 변경 + 텍스트 변경 허용
        if (isOwner) {
            try {
                // 타입 변경
                if (request.getType() != null && !request.getType().equals(piece.getType())) {
                    updatedFields.put("type", new PieceUpdateResponse.UpdateField(piece.getType(), request.getType()));

                    // 기존 미디어 삭제
                    if (piece.getMediaId() != null && piece.getPublicId() != null) {
                        String type = piece.getType();
                        String resourceType = ("VIDEO".equals(type) || "AUDIO".equals(type)) ? "video" : "image";
                        boolean deleted = cloudinaryService.deleteFromCloud(piece.getPublicId(), resourceType);
                        if (!deleted) logger.warn("기존 파일이 이미 삭제되었거나 존재하지 않음. publicId: {}", piece.getPublicId());
                    }

                    if ("TEXT".equals(request.getType())) {
                        if (request.getText() == null || request.getText().trim().isEmpty()) {
                            throw new RuntimeException("TEXT 타입으로 변경 시 내용은 필수입니다.");
                        }
                        updatedFields.put("text", new PieceUpdateResponse.UpdateField(piece.getText(), request.getText()));
                        piece.setText(request.getText());
                        piece.setMediaId(null);
                    } else {
                        if (file == null) throw new RuntimeException("파일이 필수입니다.");
                        CloudinaryUploadResponse response = cloudinaryService.uploadToCloud(file);
                        updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), response.getUrl()));
                        piece.setMediaId(response.getUrl());
                        piece.setText(null);
                    }

                    piece.setType(request.getType());
                }

                // 파일 교체
                if (file != null && !"TEXT".equals(piece.getType())) {
                    if (piece.getMediaId() != null && piece.getPublicId() != null) {
                        String type = piece.getType();
                        String resourceType = ("VIDEO".equals(type) || "AUDIO".equals(type)) ? "video" : "image";
                        boolean deleted = cloudinaryService.deleteFromCloud(piece.getPublicId(), resourceType);
                        if (!deleted) logger.warn("기존 파일이 이미 삭제되었거나 존재하지 않음. publicId: {}", piece.getPublicId());
                    }

                    CloudinaryUploadResponse response = cloudinaryService.uploadToCloud(file);
                    updatedFields.put("mediaId", new PieceUpdateResponse.UpdateField(piece.getMediaId(), response.getUrl()));
                    piece.setMediaId(response.getUrl());
                }
            } catch (IOException e) {
                logger.error("파일 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("파일 업로드 실패", e);
            }

            // 텍스트 수정
            if (request.getText() != null && "TEXT".equals(piece.getType()) && !request.getText().equals(piece.getText())) {
                updatedFields.put("text", new PieceUpdateResponse.UpdateField(piece.getText(), request.getText()));
                piece.setText(request.getText());
            }
        }

        // 본인 & 관리자 공통 허용: 태그 수정
        if (request.getTags() != null && !request.getTags().equals(piece.getTags())) {
            updatedFields.put("tags", new PieceUpdateResponse.UpdateField(piece.getTags(), request.getTags()));
            piece.setTags(request.getTags());
        }

        // 본인만 허용: 위치, 공개 여부
        if (isOwner) {
            if (request.getLocation() != null && !request.getLocation().equals(piece.getLocation())) {
                updatedFields.put("location", new PieceUpdateResponse.UpdateField(piece.getLocation(), request.getLocation()));
                piece.setLocation(request.getLocation());
            }

            if (request.getPrivatePiece() != null && !request.getPrivatePiece().equals(piece.isPrivatePiece())) {
                updatedFields.put("privatePiece", new PieceUpdateResponse.UpdateField(piece.isPrivatePiece(), request.getPrivatePiece()));
                piece.setPrivatePiece(request.getPrivatePiece());
            }
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
            .orElseThrow(() -> new RuntimeException("존재하지 않는 조각입니다."));

        if (piece.isDeleted()) {
            throw new RuntimeException("존재하지 않는 조각입니다.");
        }

        piece.setDeleted(true);
        pieceRepository.save(piece);

        return PieceDeleteResponse.builder()
                .id(piece.getId())
                .userId(piece.getUserId())
                .build();
    }

}
