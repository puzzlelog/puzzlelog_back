package com.puzzlelog.api.dto.request.diary.meta;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiarySearchRequest {
    private String userId;
    private String title;
    private String emotionContentId;
    private String backgroundContentId;
    private Boolean isShared;
    private Boolean openAt; // true: 타임캡슐만, false: 일반 일기만, null: 전체
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate updatedAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate updatedAtTo;
    private Boolean isDeleted;
    private Boolean today; // 오늘 생성된 일기만 조회
}
