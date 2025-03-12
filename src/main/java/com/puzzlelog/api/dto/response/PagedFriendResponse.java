package com.puzzlelog.api.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.puzzlelog.api.dao.entity.Friend;

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
public class PagedFriendResponse {
    private List<FriendInfo> friends;
    private Pagination pagination;

    public static PagedFriendResponse from(Page<Friend> page) {
        return PagedFriendResponse.builder()
            .friends(page.getContent().stream().map(FriendInfo::from).collect(Collectors.toList()))
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