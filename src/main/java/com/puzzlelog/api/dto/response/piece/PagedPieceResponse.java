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

/**
 * 조각 목록 조회 응답 DTO입니다.
 * 조각 리스트와 페이징 정보를 함께 제공합니다.
 * JPA 또는 MongoDB에서 공통적으로 사용됩니다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedPieceResponse {

    /** 조각 응답 리스트 */
    private List<PieceResponse> pieces;

    /** 페이징 메타 정보 (페이지 번호, 전체 개수 등) */
    private Pagination pagination;

    /**
     * JPA용 페이징 응답 생성자
     * Page<PieceResponse> 객체로부터 응답 객체를 생성합니다.
     *
     * @param page Page 객체
     * @return 변환된 PagedPieceResponse
     */
    public static PagedPieceResponse from(Page<PieceResponse> page) {
        return PagedPieceResponse.builder()
                .pieces(page.getContent())
                .pagination(Pagination.from(page))
                .build();
    }

    /**
     * MongoDB용 페이징 응답 생성자
     * Piece 리스트와 직접 전달된 페이징 정보를 이용하여 응답 객체를 생성합니다.
     *
     * @param pieceList 조각 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 개수
     * @return 변환된 PagedPieceResponse
     */
    public static PagedPieceResponse of(List<Piece> pieceList, int page, int size, long totalElements) {
        return PagedPieceResponse.builder()
                .pieces(pieceList.stream().map(PieceResponse::from).collect(Collectors.toList()))
                .pagination(Pagination.of(page, size, totalElements))
                .build();
    }
}
