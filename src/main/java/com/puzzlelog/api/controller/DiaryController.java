package com.puzzlelog.api.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.request.diary.element.DiaryElementsOrderUpdateRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiaryMetaUpdateRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiaryRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiarySearchRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementsOrderResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryDeleteResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryDetailResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryMetaUpdateResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryResponse;
import com.puzzlelog.api.dto.response.diary.meta.PagedDiaryResponse;
import com.puzzlelog.api.service.DiaryService;

import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // 일기 생성
    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponse>> createDiary(
            @Valid @RequestBody DiaryRequest request) {
        DiaryResponse response = diaryService.createDiary(request);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 생성 성공"));
    }

    // 일기 상세 조회 [요소 반환 O (일기 전체 정보 + 요소 전체 리스트 반환)]
    @GetMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<DiaryDetailResponse>> getDiary(
            @PathVariable String diaryId) {
        DiaryDetailResponse response = diaryService.getDiary(diaryId);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 조회 성공"));
    }

    // 요소 반환 X [일기 제목, 작성 날짜, emotion, 배경 등 간단한 메타 정보만 반환]
    @GetMapping
    public ResponseEntity<ApiResponse<PagedDiaryResponse<?>>> getDiaries(
            @ModelAttribute DiarySearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeElements) {

        PagedDiaryResponse<?> response = diaryService.getDiaries(request, page, size, includeElements);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 목록 조회 성공"));
    }

    // 특정 일기 메타 정보 수정
    @PatchMapping("/{diaryId}/meta")
    public ResponseEntity<ApiResponse<DiaryMetaUpdateResponse>> updateDiaryMeta(
            @PathVariable String diaryId,
            @RequestBody DiaryMetaUpdateRequest request) {

        DiaryMetaUpdateResponse response = diaryService.updateDiaryMeta(diaryId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 메타 정보 수정 성공"));
    }

    // 일기 요소 순서 변경
    @PatchMapping("/{diaryId}/elements-orders")
    public ResponseEntity<ApiResponse<DiaryElementsOrderResponse>> updateDiaryElements(
            @PathVariable String diaryId,
            @RequestBody DiaryElementsOrderUpdateRequest request) {

        DiaryElementsOrderResponse response = diaryService.updateDiaryElements(diaryId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 요소 순서 수정 성공"));
    }

    // 특정 일기 삭제 (논리 삭제)
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<DiaryDeleteResponse>> deleteDiary(
            @PathVariable String diaryId) {
        DiaryDeleteResponse response = diaryService.deleteDiary(diaryId);
        return ResponseEntity.ok(ApiResponse.success(response, "일기 삭제 성공"));
    }
}
