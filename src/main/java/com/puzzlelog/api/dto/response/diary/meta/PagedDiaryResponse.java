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
public class PagedDiaryResponse<T> {

    private List<T> diaries;
    private Pagination pagination;

    // JPA 사용시 메서드
    public static <T> PagedDiaryResponse<T> from(Page<T> page) {
        return PagedDiaryResponse.<T>builder()
                .diaries(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }
    
    // MongoDB 사용시 메서드
    public static <T> PagedDiaryResponse<T> of(List<T> diaries, int currentPage, int size, long totalElements) {
        return PagedDiaryResponse.<T>builder()
                .diaries(diaries)
                .pagination(Pagination.of(currentPage, size, totalElements))
                .build();
    }
}