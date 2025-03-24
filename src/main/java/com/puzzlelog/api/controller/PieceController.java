package com.puzzlelog.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	
    // 조각 파일 크기 체크
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkFileSize(
            @RequestHeader(value = "Content-Length", required = false) Long fileSize,
            @RequestHeader(value = "Content-Type", required = false) String fileType) {

        System.out.println("🔍 Content-Length: " + fileSize + " bytes");
        System.out.println("🔍 Content-Type: " + fileType);

        if (fileSize == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build(); // Content-Length가 없는 경우 400 Bad Request 반환
        }

        if (fileSize > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        return ResponseEntity.ok().build();
    }
    
	// 조각 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PieceResponse>> createPiece(
            @RequestPart("data") PieceRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        System.out.println("✅ JSON 데이터: " + request);
        System.out.println("✅ 파일: " + (file != null ? file.getOriginalFilename() : "파일 없음"));

        // 서비스 호출 시 request에서 받은 userId를 직접 전달
        PieceResponse response = pieceService.addPiece(request, file);

        return ResponseEntity.ok(ApiResponse.success(response, "조각이 생성되었습니다."));
    }

	// 조각 단일 조회
	@GetMapping("/{pieceId}")
	public ResponseEntity<ApiResponse<PieceResponse>> getPiece(@PathVariable String pieceId) {
	    PieceResponse response = pieceService.getPiece(pieceId);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각을 조회하는데 성공했습니다."));
	}

	// 조각 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<PagedPieceResponse>> getPieces(
	    @ModelAttribute PieceSearchRequest request,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "20") int size
	) {
	    PagedPieceResponse response = pieceService.searchPieces(request, page, size);

	    if (response.getPieces().isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(ApiResponse.fail("조건에 맞는 조각이 없습니다."));
	    }

	    String message = request.hasNoCondition() ? "전체 조각 조회 성공" : "조각 검색 성공";
	    return ResponseEntity.ok(ApiResponse.success(response, message));
	}

	// 조각 수정
	@PatchMapping(value = "/{pieceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<PieceUpdateResponse>> updatePiece(
	        @PathVariable String pieceId,
	        @RequestPart("data") PieceUpdateRequest request,
	        @RequestPart(value = "file", required = false) MultipartFile file
	) {
	    PieceUpdateResponse response = pieceService.updatePiece(pieceId, request, file);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각 수정 성공"));
	}

	// 조각 삭제 (비활성화 처리)
	@DeleteMapping("/{pieceId}")
	public ResponseEntity<ApiResponse<PieceDeleteResponse>> deletePiece(@PathVariable String pieceId) {
	    PieceDeleteResponse response = pieceService.deletePiece(pieceId);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각 삭제 성공"));
	}
}
