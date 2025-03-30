package com.puzzlelog.api.repository.listsearch;

import com.puzzlelog.api.dto.request.piece.PieceSearchRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * 조각 목록 검색 조건을 MongoDB Criteria로 변환하는 클래스입니다.
 * PieceSearchRequest의 각 필드를 기반으로 Criteria를 생성합니다.
 */
@Component
public class PieceListSearch implements ListSearch<PieceSearchRequest, Criteria> {

    @Override
    public Criteria buildSearch(PieceSearchRequest request) {
        Criteria criteria = new Criteria();

        // 작성자 ID 조건
        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            criteria.and("userId").is(request.getUserId());
        }

        // 조각 타입 조건 (TEXT, IMAGE, VIDEO, AUDIO)
        if (request.getType() != null) {
            criteria.and("type").is(request.getType());
        }

        // 텍스트 검색 (부분 일치, 대소문자 구분 없음)
        if (request.getContent() != null) {
            criteria.and("content").regex(".*" + request.getContent() + ".*", "i");
        }

        // 태그 목록 조건 (하나라도 포함되면 매칭)
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            criteria.and("tags").in(request.getTags());
        }

        // 비공개 여부 조건 (privatePiece = true/false)
        // DTO에서 기본값을 제거하고, 명시적으로 들어올 때만 적용합니다.
        if (request.getPrivatePiece() != null) {
            criteria.and("privatePiece").is(request.getPrivatePiece());
        }

        // 삭제 여부 조건 (deleted = true/false)
        if (request.getDeleted() != null) {
            criteria.and("deleted").is(request.getDeleted());
        }

        // 생성일 조건 (createdAtFrom ~ createdAtTo)
        if (request.getCreatedAtFrom() != null || request.getCreatedAtTo() != null) {
            Instant fromInstant;
            Instant toInstant;

            if (request.getCreatedAtFrom() != null && request.getCreatedAtTo() != null) {
                fromInstant = request.getCreatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
                toInstant = request.getCreatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

                if (fromInstant.isAfter(toInstant)) {
                    throw new IllegalArgumentException("createdAtFrom 날짜는 createdAtTo 날짜보다 이전이어야 합니다.");
                }
            } else if (request.getCreatedAtFrom() != null) {
                fromInstant = request.getCreatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
                toInstant = Instant.now();
            } else {
                fromInstant = Instant.ofEpochSecond(0);
                toInstant = request.getCreatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            }

            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        }

        return criteria;
    }
}
