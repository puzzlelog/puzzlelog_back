package com.puzzlelog.api.dto.response.friend;

import java.util.List;
import org.springframework.data.domain.Page;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedFriendResponse {
    private List<FriendDetailResponse> friends;
    private Pagination pagination;

    public static PagedFriendResponse from(Page<FriendDetailResponse> page) {
        return PagedFriendResponse.builder()
            .friends(page.getContent())
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
}
