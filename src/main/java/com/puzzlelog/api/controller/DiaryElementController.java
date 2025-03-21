package com.puzzlelog.api.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.request.diary.element.DiaryElementRequest;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementSearchRequest;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementUpdateRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementDeleteResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementUpdateResponse;
import com.puzzlelog.api.dto.response.diary.element.PagedDiaryElementResponse;
import com.puzzlelog.api.service.DiaryElementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/diaries/{diaryId}/elements")
@RequiredArgsConstructor
public class DiaryElementController {

	private final DiaryElementService diaryElementService;
	
	/**
	 *  POST /elements (요소 생성 및 일기 추가)
			요소 생성과 동시에 일기에 즉시 연결됩니다.
			따라서 요청 데이터에 반드시 diaryId를 포함해야 합니다.
	 */
	
	// 요소 생성
	@PostMapping
	public ResponseEntity<ApiResponse<DiaryElementResponse>> createElement(
	        @PathVariable String diaryId,
	        @Valid @RequestBody DiaryElementRequest request) {

	    DiaryElementResponse response = diaryElementService.createDiaryElement(diaryId, request);

	    return ResponseEntity.ok(ApiResponse.success(response, "요소 생성 성공"));
	}

	// 특정 요소 개별 조회
	@GetMapping("/{elementId}")
	public ResponseEntity<ApiResponse<DiaryElementResponse>> getElement(
	        @PathVariable String diaryId,
	        @PathVariable String elementId) {

	    DiaryElementResponse response = diaryElementService.getElement(diaryId, elementId);
	    return ResponseEntity.ok(ApiResponse.success(response, "요소 조회 성공"));
	}
	
	// 요소 목록 조회 (필터링 + 페이징 지원)
	@GetMapping
	public ResponseEntity<ApiResponse<PagedDiaryElementResponse>> getElements(
	        @PathVariable String diaryId,
	        @ModelAttribute DiaryElementSearchRequest request) {

	    PagedDiaryElementResponse response = diaryElementService.getElements(diaryId, request);
	    return ResponseEntity.ok(ApiResponse.success(response, "요소 목록 조회 성공"));
	}

	// 특정 요소 수정
	@PatchMapping("/{elementId}")
	public ResponseEntity<ApiResponse<DiaryElementUpdateResponse>> updateElement(
	        @PathVariable("diaryId") String diaryId,
	        @PathVariable("elementId") String elementId,
	        @RequestBody DiaryElementUpdateRequest request) {

	    DiaryElementUpdateResponse response = diaryElementService.updateDiaryElement(diaryId, elementId, request);

	    return ResponseEntity.ok(ApiResponse.success(response, "요소 수정 성공"));
	}

	// 특정 요소 삭제 (논리 삭제)
	@DeleteMapping("/{elementId}")
	public ResponseEntity<ApiResponse<DiaryElementDeleteResponse>> deleteElement(
	        @PathVariable String diaryId,
	        @PathVariable String elementId) {

	    DiaryElementDeleteResponse response = diaryElementService.deleteDiaryElement(diaryId, elementId);

	    return ResponseEntity.ok(ApiResponse.success(response, "요소 삭제 성공"));
	}
	
}