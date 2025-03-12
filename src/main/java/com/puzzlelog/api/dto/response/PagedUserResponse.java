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
public class PagedUserResponse {
    private List<UserResponse> users;
    private Pagination pagination;

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
        private boolean isFirst;
        private boolean isLast;

        public static Pagination from(Page<?> page) {
            return Pagination.builder()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .isFirst(page.isFirst())
                    .isLast(page.isLast())
                    .build();
        }
    }

    public static PagedUserResponse from(Page<UserResponse> page) {
        return PagedUserResponse.builder()
                .users(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }
}