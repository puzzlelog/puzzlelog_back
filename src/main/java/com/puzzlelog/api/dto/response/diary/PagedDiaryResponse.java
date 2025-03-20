package com.puzzlelog.api.dto.response.diary;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

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
public class PagedDiaryResponse {
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
    private List<DiarySimpleResponse> diaries;

    public static PagedDiaryResponse from(Page<Diary> page) {
        return PagedDiaryResponse.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .diaries(page.getContent().stream()
                        .map(DiarySimpleResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
    
    public static PagedDiaryResponse of(List<Diary> diaries, int currentPage, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PagedDiaryResponse.builder()
            .totalPages(totalPages)
            .totalElements(totalElements)
            .currentPage(currentPage)
            .size(size)
            .diaries(diaries.stream()
                .map(DiarySimpleResponse::from)
                .collect(Collectors.toList()))
            .build();
    }

}
