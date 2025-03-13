package com.puzzlelog.api.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

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
public class PagedPieceResponse {

    private List<PieceResponse> pieces;
    private Pagination pagination;

    public static PagedPieceResponse from(Page<PieceResponse> page) {
        return PagedPieceResponse.builder()
                .pieces(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
        private boolean first;
        private boolean last;

        public static Pagination from(Page<?> page) {
            return Pagination.builder()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .build();
        }
    }
}
