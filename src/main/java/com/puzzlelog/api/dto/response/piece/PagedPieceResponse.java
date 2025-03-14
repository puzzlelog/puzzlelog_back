package com.puzzlelog.api.dto.response.piece;

import java.util.List;
import java.util.stream.Collectors;

import com.puzzlelog.api.dao.document.Piece;

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

    // 기존 메서드 유지 (JPA 사용 시 편의 메서드)
    public static PagedPieceResponse from(org.springframework.data.domain.Page<PieceResponse> page) {
        return PagedPieceResponse.builder()
                .pieces(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }

    // MongoDB에서 사용할 새 메서드 추가
    public static PagedPieceResponse of(List<Piece> pieceList, int page, int size, long totalElements) {
        return PagedPieceResponse.builder()
            .pieces(pieceList.stream().map(PieceResponse::from).collect(Collectors.toList()))
            .pagination(Pagination.builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= totalElements)
                .build())
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

        public static Pagination from(org.springframework.data.domain.Page<?> page) {
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
