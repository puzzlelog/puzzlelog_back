package com.puzzlelog.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dto.request.diary.DiaryRequest;
import com.puzzlelog.api.dto.request.diary.DiarySearchRequest;
import com.puzzlelog.api.dto.response.diary.DiaryDetailResponse;
import com.puzzlelog.api.dto.response.diary.DiaryResponse;
import com.puzzlelog.api.dto.response.diary.PagedDiaryResponse;
import com.puzzlelog.api.repository.mongo.DiaryElementRepository;
import com.puzzlelog.api.repository.mongo.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryElementRepository diaryElementRepository;
    private final MongoTemplate mongoTemplate;

    // 일기 생성
    @Transactional
    public DiaryResponse createDiary(DiaryRequest request) {

        // [타임캡슐 처리] openAt 날짜와 timeZone 기준으로 Instant 처리
        Instant openAtInstant = null;
        if (request.getOpenAt() != null && !request.getOpenAt().isBlank()) {
            ZoneId userZone = ZoneId.of("Asia/Seoul"); // 기본값 한국 타임존
            if (request.getTimeZone() != null && !request.getTimeZone().isBlank()) {
                userZone = ZoneId.of(request.getTimeZone());
            }

            LocalDate date = LocalDate.parse(request.getOpenAt());
            openAtInstant = date.atStartOfDay(userZone).toInstant();
        }

        // [일기 생성] Diary 객체 생성 및 저장
        Diary diary = Diary.builder()
            .userId(request.getUserId())
            .title(request.getTitle())
            .backgroundContentId(request.getBackgroundContentId())
            .themeColor(request.getThemeColor())
            .emotionContentId(request.getEmotionContentId())
            .isShared(Optional.ofNullable(request.getIsShared()).orElse(false))
            .openAt(openAtInstant) // 타임캡슐이면 처리된 Instant, 일반 일기는 null
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .isDeleted(false)
            .build();

        diaryRepository.save(diary);
        
        // [유효성 검사] 요소 타입과 contentId/drawingData 검증
        final List<String> allowedTypes = List.of("TEXT", "IMAGE", "AUDIO", "VIDEO", "STICKER", "DRAWING");

        request.getElements().forEach(elementRequest -> {
            String elementType = elementRequest.getElementType();

            // elementType이 허용된 타입인지 검사
            if (!allowedTypes.contains(elementType)) {
                throw new IllegalArgumentException("허용되지 않는 요소 타입입니다: " + elementType);
            }
            
            if ("DRAWING".equals(elementType)) {
                // DRAWING 타입은 drawingData 필수, contentId 지정 불가
                if (elementRequest.getDrawingData() == null || elementRequest.getDrawingData().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입의 요소는 drawingData가 필수입니다.");
                }
                if (elementRequest.getContentId() != null && !elementRequest.getContentId().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입의 요소는 contentId를 지정할 수 없습니다.");
                }
            } else {
                // DRAWING이 아닌 타입은 contentId 필수, drawingData 지정 불가
                if (elementRequest.getContentId() == null || elementRequest.getContentId().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입이 아닌 요소는 contentId가 필수입니다.");
                }
                if (elementRequest.getDrawingData() != null && !elementRequest.getDrawingData().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입이 아닌 요소는 drawingData를 지정할 수 없습니다.");
                }
            }
        });

        // [요소 생성] DiaryElement 객체 생성 및 저장 후 Element ID 리스트 반환
        List<String> savedElementIds = request.getElements().stream()
            .map(elementRequest -> {
            	DiaryElement element = DiaryElement.builder()
            		    .diaryId(diary.getId())
            		    .elementType(elementRequest.getElementType())
            		    .contentId(elementRequest.getContentId())
            		    .drawingData(elementRequest.getDrawingData())
            		    .elementOrder(elementRequest.getElementOrder())
            		    .position(Optional.ofNullable(elementRequest.getPosition()).orElse(List.of(0.0, 0.0)))
            		    .scale(Optional.ofNullable(elementRequest.getScale()).orElse(1.0))
            		    .rotation(Optional.ofNullable(elementRequest.getRotation()).orElse(0.0))
            		    .createdAt(Instant.now())
            		    .updatedAt(Instant.now())
            		    // TODO: 추후 요소 편집 정보(ElementDecoration) 붙일 위치
            		    // .decoration(elementRequest.getDecoration())
            		    .build();  // ✅ 빌더 호출 종료를 명확히 함

                diaryElementRepository.save(element);
                return element.getElementId();
            })
            .collect(Collectors.toList());

        diary.setElementIds(savedElementIds);
        diaryRepository.save(diary); // 업데이트된 Diary 저장

        return DiaryResponse.from(diary);
    }
    
    // 개별 일기 상세 조회 (수정시 불러올 때 사용)
    @Transactional(readOnly = true)
    public DiaryDetailResponse getDiary(String diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

        List<DiaryElement> elements = diaryElementRepository.findAllByDiaryIdOrderByElementOrderAsc(diaryId);

        return DiaryDetailResponse.from(diary, elements);
    }
    
    // 일기 목록 조회
    @Transactional(readOnly = true)
    public PagedDiaryResponse getDiaries(DiarySearchRequest request, int page, int size) {

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

        if (request.getIsShared() != null) {
            criteria.and("isShared").is(request.getIsShared());
        }

        if (request.getIsDeleted() != null) {
            criteria.and("isDeleted").is(request.getIsDeleted());
        }

        // openAt 여부에 따라 타임캡슐 필터링
        if (request.getOpenAt() != null) {
            if (request.getOpenAt()) {
                criteria.and("openAt").ne(null);
            } else {
                criteria.and("openAt").is(null);
            }
        }

        // createdAt 날짜 필터
        if (request.getCreatedAt() != null) {
            Instant fromInstant = request.getCreatedAt().atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toInstant = request.getCreatedAt().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        } else {
            if (request.getCreatedAtFrom() != null || request.getCreatedAtTo() != null) {
                Instant fromInstant = request.getCreatedAtFrom() != null
                    ? request.getCreatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant()
                    : Instant.ofEpochSecond(0);

                Instant toInstant = request.getCreatedAtTo() != null
                    ? request.getCreatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                    : Instant.now();

                criteria.and("createdAt").gte(fromInstant).lt(toInstant);
            }
        }

        // updatedAt 날짜 필터
        if (request.getUpdatedAtFrom() != null || request.getUpdatedAtTo() != null) {
            Instant fromInstant = request.getUpdatedAtFrom() != null
                ? request.getUpdatedAtFrom().atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.ofEpochSecond(0);

            Instant toInstant = request.getUpdatedAtTo() != null
                ? request.getUpdatedAtTo().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.now();

            criteria.and("updatedAt").gte(fromInstant).lt(toInstant);
        }

        // 오늘 생성된 일기만 조회 (today=true일 때)
        if (request.getToday() != null && request.getToday()) {
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Instant fromInstant = today.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            Instant toInstant = today.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            criteria.and("createdAt").gte(fromInstant).lt(toInstant);
        }

        Query query = new Query(criteria)
            .with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Diary> diaries = mongoTemplate.find(query, Diary.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Diary.class);

        return PagedDiaryResponse.of(diaries, page, size, total);
    }
    
}