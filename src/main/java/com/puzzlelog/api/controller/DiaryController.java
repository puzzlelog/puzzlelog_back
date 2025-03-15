package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.config.ApiResponse;
import com.puzzlelog.api.dto.request.diary.DiaryRequest;
import com.puzzlelog.api.dto.response.diary.DiaryResponse;

@RestController
@RequestMapping("/diaries")
public class DiaryController {

    // 일기 생성
    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponse>> createDiary(@RequestBody DiaryRequest request) {
        // Service 구현 예정
        return ResponseEntity.ok(ApiResponse.success(null, "일기 생성 성공"));
    }

//    // 일기 목록 조회 (필터링 및 페이징 가능)
//    @GetMapping
//    public ResponseEntity<ApiResponse<PagedDiaryResponse>> getDiaries(
//            DiarySearchRequest request,  // 👈 다양한 조건을 위한 DTO로 수정
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        // Service 구현 예정
//        return ResponseEntity.ok(ApiResponse.success(null, "일기 목록 조회 성공"));
//    }
//
//    // 특정 일기 조회
//    @GetMapping("/{diaryId}")
//    public ResponseEntity<ApiResponse<DiaryResponse>> getDiary(@PathVariable String diaryId) {
//        // Service 구현 예정
//        return ResponseEntity.ok(ApiResponse.success(null, "일기 조회 성공"));
//    }
//
//    // 특정 일기 수정
//    @PatchMapping("/{diaryId}")
//    public ResponseEntity<ApiResponse<DiaryResponse>> updateDiary(
//            @PathVariable String diaryId,
//            @RequestBody DiaryUpdateRequest request) {
//
//        // Service 구현 예정
//        return ResponseEntity.ok(ApiResponse.success(null, "일기 수정 성공"));
//    }
//
//    // 특정 일기 삭제 (논리 삭제)
//    @DeleteMapping("/{diaryId}")
//    public ResponseEntity<ApiResponse<Void>> deleteDiary(@PathVariable String diaryId) {
//        // Service 구현 예정
//        return ResponseEntity.ok(ApiResponse.successMessage("일기 삭제 성공"));
//    }
}
