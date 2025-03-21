package com.puzzlelog.api.repository.listsearch;

import com.puzzlelog.api.dto.request.piece.PieceSearchRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class PieceListSearch implements ListSearch<PieceSearchRequest, Criteria> { // 조각 목록 조회

    @Override
    public Criteria buildSearch(PieceSearchRequest request) {
        Criteria criteria = new Criteria();

        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            criteria.and("userId").is(request.getUserId());
        }

        if (request.getType() != null) {
            criteria.and("type").is(request.getType());
        }

        if (request.getContent() != null) {
            criteria.and("content").regex(".*" + request.getContent() + ".*", "i");
        }

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            criteria.and("tags").in(request.getTags());
        }

        if (request.getIsPrivate() != null) {
            criteria.and("isPrivate").is(request.getIsPrivate());
        }

        if (request.getIsDeleted() != null) {
            criteria.and("isDeleted").is(request.getIsDeleted());
        }

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
