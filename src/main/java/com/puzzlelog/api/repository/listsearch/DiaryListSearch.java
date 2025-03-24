package com.puzzlelog.api.repository.listsearch;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.puzzlelog.api.dto.request.diary.meta.DiarySearchRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class DiaryListSearch implements ListSearch<DiarySearchRequest, Criteria> { // 일기 목록 조회

    @Override
    public Criteria buildSearch(DiarySearchRequest request) {
        Criteria criteria = new Criteria();

        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            criteria.and("userId").is(request.getUserId());
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            criteria.and("title").regex(".*" + request.getTitle() + ".*", "i");
        }

        if (request.getEmotionContentId() != null) {
            criteria.and("emotionContentId").is(request.getEmotionContentId());
        }

        if (request.getBackgroundContentId() != null) {
            criteria.and("backgroundContentId").is(request.getBackgroundContentId());
        }

        if (request.getShared() != null) {
            criteria.and("shared").is(request.getShared());
        }

        if (request.getDeleted() != null) {
            criteria.and("deleted").is(request.getDeleted());
        }

        if (request.getOpenAt() != null) {
            if (request.getOpenAt()) {
                criteria.and("openAt").ne(null);
            } else {
                criteria.and("openAt").is(null);
            }
        }

        if (request.getCreatedAt() != null) {
            Instant fromInstant = request.getCreatedAt().atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toInstant = request.getCreatedAt().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        } else if (request.getCreatedAtFrom() != null || request.getCreatedAtTo() != null) {
            Instant fromInstant;
            Instant toInstant;

            if (request.getCreatedAtFrom() != null && request.getCreatedAtTo() != null) {
                fromInstant = request.getCreatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
                toInstant = request.getCreatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

                if (fromInstant.isAfter(toInstant)) {
                    throw new IllegalArgumentException("createdAtFrom은 createdAtTo보다 이전 날짜여야 합니다.");
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

        if (request.getUpdatedAtFrom() != null || request.getUpdatedAtTo() != null) {
            Instant fromInstant;
            Instant toInstant;

            if (request.getUpdatedAtFrom() != null && request.getUpdatedAtTo() != null) {
                fromInstant = request.getUpdatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
                toInstant = request.getUpdatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

                if (fromInstant.isAfter(toInstant)) {
                    throw new IllegalArgumentException("updatedAtFrom은 updatedAtTo보다 이전 날짜여야 합니다.");
                }
            } else if (request.getUpdatedAtFrom() != null) {
                fromInstant = request.getUpdatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
                toInstant = Instant.now();
            } else {
                fromInstant = Instant.ofEpochSecond(0);
                toInstant = request.getUpdatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            }

            criteria.and("updatedAt").gte(fromInstant).lt(toInstant);
        }

        if (request.getToday() != null && request.getToday()) {
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Instant fromInstant = today.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            Instant toInstant = today.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        }

        return criteria;
    }
}
