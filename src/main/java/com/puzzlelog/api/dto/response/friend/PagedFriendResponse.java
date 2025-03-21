package com.puzzlelog.api.dto.response.friend;

import java.util.List;
import org.springframework.data.domain.Page;

import com.puzzlelog.api.dto.response.common.Pagination;

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
}