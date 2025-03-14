package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.config.ApiResponse;
import com.puzzlelog.api.dto.request.PieceRequest;
import com.puzzlelog.api.dto.request.PieceUpdateRequest;
import com.puzzlelog.api.dto.response.PagedPieceResponse;
import com.puzzlelog.api.dto.response.PieceDeleteResponse;
import com.puzzlelog.api.dto.response.PieceResponse;
import com.puzzlelog.api.service.PieceService;

@RestController
@RequestMapping("/api/pieces")
public class PieceController {

	private final PieceService pieceService;

    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }
	
	// 조각 생성
	@PostMapping
	public ResponseEntity<ApiResponse<PieceResponse>> createPiece(@RequestBody PieceRequest request) {
	    PieceResponse response = pieceService.addPiece(request);
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
	        @RequestParam(required = false) Integer userId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "20") int size
	) {
	    PagedPieceResponse response = pieceService.getPieces(userId, page, size);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각의 목록을 조회하는데 성공했습니다."));
	}

	// 조각 수정
	@PatchMapping("/{pieceId}")
	public ResponseEntity<ApiResponse<PieceResponse>> updatePiece(
	        @PathVariable String pieceId,
	        @RequestBody PieceUpdateRequest request) {

		PieceResponse response = pieceService.updatePiece(pieceId, request);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각 수정 성공"));
	}

	// 조각 삭제 (비활성화 처리)
	@DeleteMapping("/{pieceId}")
	public ResponseEntity<ApiResponse<PieceDeleteResponse>> deletePiece(@PathVariable String pieceId) {
	    PieceDeleteResponse response = pieceService.deletePiece(pieceId);
	    return ResponseEntity.ok(ApiResponse.success(response, "조각 삭제 성공"));
	}
}
