package com.puzzlelog.api.dto.response.diary.meta;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dto.response.common.Pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedDiaryResponse {

    private List<DiarySimpleResponse> diaries;
    private Pagination pagination;

    // JPA 사용시 메서드
    public static PagedDiaryResponse from(Page<Diary> page) {
        return PagedDiaryResponse.builder()
                .diaries(page.getContent().stream()
                        .map(DiarySimpleResponse::from)
                        .collect(Collectors.toList()))
                .pagination(Pagination.from(page))
                .build();
    }
    
    // 몽고DB 사용시 메서드
    public static PagedDiaryResponse of(List<Diary> diaries, int currentPage, int size, long totalElements) {
        return PagedDiaryResponse.builder()
                .diaries(diaries.stream()
                        .map(DiarySimpleResponse::from)
                        .collect(Collectors.toList()))
                .pagination(Pagination.of(currentPage, size, totalElements))
                .build();
    }
}