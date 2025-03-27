package com.puzzlelog.api.dto.response.user;

import java.util.List;

import org.springframework.data.domain.Page;

import com.puzzlelog.api.dto.response.common.Pagination;

import lombok.*;

/**
 * 사용자 목록 + 페이지네이션 정보 응답 DTO
 * 관리자 사용자 조회 API에서 사용됩니다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedUserResponse {

    /** 사용자 목록 */
    private List<UserResponse> users;

    /** 페이지네이션 정보 */
    private Pagination pagination;

    /**
     * Page 객체로부터 PagedUserResponse 생성
     *
     * @param page Spring의 Page<UserResponse>
     * @return 변환된 응답 DTO
     */
    public static PagedUserResponse from(Page<UserResponse> page) {
        return PagedUserResponse.builder()
                .users(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }
}
