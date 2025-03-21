package com.puzzlelog.api.dto.response.piece;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.puzzlelog.api.dao.document.Piece;
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
public class PagedPieceResponse {

    private List<PieceResponse> pieces;
    private Pagination pagination;

    // 기존 메서드 유지 (JPA 사용 시 편의 메서드)
    public static PagedPieceResponse from(Page<PieceResponse> page) {
        return PagedPieceResponse.builder()
                .pieces(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }

    // MongoDB에서 사용할 메서드
    public static PagedPieceResponse of(List<Piece> pieceList, int page, int size, long totalElements) {
        return PagedPieceResponse.builder()
                .pieces(pieceList.stream().map(PieceResponse::from).collect(Collectors.toList()))
                .pagination(Pagination.of(page, size, totalElements))
                .build();
    }
}