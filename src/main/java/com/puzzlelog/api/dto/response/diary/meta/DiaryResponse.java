package com.puzzlelog.api.dto.response.diary.meta;

import java.time.Instant;
import java.util.List;

import com.puzzlelog.api.dao.document.Diary;

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
public class DiaryResponse {
    private String diaryId;
    private String title;
    private List<String> elementIds;
    private Instant createdAt;
    private Instant openAt; // 타임캡슐 오픈 날짜 (일반 일기인 경우 null)

    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .elementIds(diary.getElementIds())
                .createdAt(diary.getCreatedAt())
                .openAt(diary.getOpenAt())
                .build();
    }
}
