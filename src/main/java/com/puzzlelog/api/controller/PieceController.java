package com.puzzlelog.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dto.request.piece.PieceRequest;
import com.puzzlelog.api.dto.request.piece.PieceSearchRequest;
import com.puzzlelog.api.dto.request.piece.PieceUpdateRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.piece.PagedPieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceDeleteResponse;
import com.puzzlelog.api.dto.response.piece.PieceResponse;
import com.puzzlelog.api.dto.response.piece.PieceUpdateResponse;
import com.puzzlelog.api.service.PieceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pieces")
@RequiredArgsConstructor
public class PieceController {

	private final PieceService pieceService;
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final Logger log = LoggerFactory.getLogger(PieceController.class);

    /**
     * 업로드될 파일의 크기를 사전 검사합니다.
     * <p>Content-Length 헤더를 기반으로 100MB 제한 초과 여부를 판단합니다.
     * 
     * @param fileSize 요청 헤더의 Content-Length
     * @param fileType 요청 헤더의 Content-Type
     * @return 200 OK (정상), 413 Payload Too Large (크기 초과), 400 Bad Request (헤더 누락)
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkFileSize(
            @RequestHeader(value = "Content-Length", required = false) Long fileSize,
            @RequestHeader(value = "Content-Type", required = false) String fileType) {

        log.debug("🔍 Content-Length: {} bytes", fileSize);
        log.debug("🔍 Content-Type: {}", fileType);

        if (fileSize == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Content-Length 누락
        }

        if (fileSize > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        return ResponseEntity.ok().build();
    }
    
    /**
     * 조각을 생성합니다.
     * multipart/form-data 형식으로 JSON 데이터와 파일을 함께 전송합니다.
     * 인증된 사용자만 사용할 수 있으며, userId는 JWT에서 자동 추출됩니다.
     *
     * @param request 조각 생성 요청 데이터 (텍스트, 타입, 태그 등)
     * @param file (선택) 이미지, 오디오, 비디오 파일
     * @return 생성된 조각 정보를 포함한 성공 응답
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PieceResponse>> createPiece(
            @RequestPart("data") PieceRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 인증된 사용자 ID를 요청 객체에 주입
        request.setUserId(authenticatedUserId);

        log.info("✅ 조각 생성 요청 - 사용자: {}, 타입: {}, 파일명: {}",
            authenticatedUserId,
            request.getType(),
            (file != null ? file.getOriginalFilename() : "파일 없음")
        );

        PieceResponse response = pieceService.addPiece(request, file);

        return ResponseEntity.ok(ApiResponse.success(response, "조각이 생성되었습니다."));
    }

    /**
     * 조각 단일 조회 API입니다.
     * 조각 ID로 상세 정보를 조회합니다.
     * 비공개 조각일 경우 소유자 본인만 조회할 수 있습니다.
     *
     * @param pieceId 조각 ID (MongoDB _id)
     * @return 조각 정보
     */
    @GetMapping("/{pieceId}")
    public ResponseEntity<ApiResponse<PieceResponse>> getPiece(@PathVariable String pieceId) {
        String requesterId = SecurityContextHolder.getContext().getAuthentication().getName();

        PieceResponse response = pieceService.getPiece(pieceId);

        // 비공개 조각은 소유자만 접근 가능
        if (response.isPrivatePiece() && !response.getUserId().equals(requesterId)) {
            throw new RuntimeException("비공개 조각에 접근할 수 없습니다.");
        }

        return ResponseEntity.ok(ApiResponse.success(response, "조각을 조회하는데 성공했습니다."));
    }

    /**
     * 조각 목록 조회 API입니다.
     * 검색 조건에 따라 전체 공개 조각 또는 특정 사용자의 조각을 페이징하여 반환합니다.
     * 비공개 조각은 소유자 본인만 조회할 수 있습니다.
     *
     * @param request 검색 조건 (userId, type, tag 등)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 조건에 맞는 조각 목록 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedPieceResponse>> getPieces(
        @ModelAttribute PieceSearchRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        // 현재 요청자의 userId (JWT에서 추출)
        String requesterId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 요청자의 역할 (ROLE_USER, ROLE_ADMIN 등)
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream()
            .findFirst()
            .map(authority -> authority.getAuthority())
            .orElse("ROLE_USER");

        // 조건에 따른 비공개 조각 필터링 여부는 서비스에서 처리
        PagedPieceResponse response = pieceService.searchPieces(request, page, size, requesterId, role);

        if (response.getPieces().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("조건에 맞는 조각이 없습니다."));
        }

        String message = request.hasNoCondition() ? "전체 조각 조회 성공" : "조각 검색 성공";
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    /**
     * 조각을 수정합니다.
     * 본인은 전체 수정이 가능하며, 관리자는 tags만 수정할 수 있습니다.
     *
     * @param pieceId 수정할 조각의 ID
     * @param request 수정 요청 데이터
     * @param file (선택) 새로운 미디어 파일
     * @return 수정된 필드 정보를 포함한 응답
     */
    @PatchMapping(value = "/{pieceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PieceUpdateResponse>> updatePiece(
            @PathVariable String pieceId,
            @RequestPart("data") PieceUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        String requesterId = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");

        PieceUpdateResponse response = pieceService.updatePiece(pieceId, request, file, requesterId, role);
        return ResponseEntity.ok(ApiResponse.success(response, "조각 수정 성공"));
    }


	// 조각 삭제 (비활성화 처리)
	@DeleteMapping("/{pieceId}")
	public ResponseEntity<ApiResponse<PieceDeleteResponse>> deletePiece(@PathVariable String pieceId) {
	    PieceDeleteResponse response = pieceService.deletePiece(pieceId);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각 삭제 성공"));
	}
}
