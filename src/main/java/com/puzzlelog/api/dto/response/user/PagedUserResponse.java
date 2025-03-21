package com.puzzlelog.api.dto.response.user;

import java.util.List;

import org.springframework.data.domain.Page;

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
public class PagedUserResponse {
    private List<UserResponse> users;
    private Pagination pagination;

    public static PagedUserResponse from(Page<UserResponse> page) {
        return PagedUserResponse.builder()
                .users(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }
}
