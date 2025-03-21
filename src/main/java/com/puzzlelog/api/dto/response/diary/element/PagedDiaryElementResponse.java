package com.puzzlelog.api.dto.response.diary.element;

import java.util.List;
import java.util.stream.Collectors;

import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dto.response.common.Pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedDiaryElementResponse {
    private List<DiaryElementResponse> elements;
    private Pagination pagination;

    public static PagedDiaryElementResponse of(List<DiaryElement> elements, int page, int size, long totalElements) {
        return PagedDiaryElementResponse.builder()
            .elements(elements.stream()
                .map(DiaryElementResponse::from)
                .collect(Collectors.toList()))
            .pagination(Pagination.of(page, size, totalElements))
            .build();
    }
}
