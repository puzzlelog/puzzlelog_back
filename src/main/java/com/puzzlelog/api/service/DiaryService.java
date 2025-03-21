package com.puzzlelog.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementsOrderUpdateRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiaryMetaUpdateRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiaryRequest;
import com.puzzlelog.api.dto.request.diary.meta.DiarySearchRequest;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementsOrderResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryDeleteResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryDetailResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryMetaUpdateResponse;
import com.puzzlelog.api.dto.response.diary.meta.DiaryResponse;
import com.puzzlelog.api.dto.response.diary.meta.PagedDiaryResponse;
import com.puzzlelog.api.repository.listsearch.DiaryListSearch;
import com.puzzlelog.api.repository.mongo.DiaryElementRepository;
import com.puzzlelog.api.repository.mongo.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryElementRepository diaryElementRepository;
    private final MongoTemplate mongoTemplate;
    private final DiaryListSearch diaryListSearch;

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
        
        // [유효성 검사] 요소 타입과 contentId/drawingData 검증 (간결화 버전)
        final List<String> allowedTypes = List.of("TEXT", "IMAGE", "AUDIO", "VIDEO", "STICKER", "DRAWING");

        request.getElements().forEach(elementRequest -> {
            if (!allowedTypes.contains(elementRequest.getElementType())) {
                throw new IllegalArgumentException("허용되지 않는 요소 타입입니다: " + elementRequest.getElementType());
            }
            if (!elementRequest.isValidByType()) {
                throw new IllegalArgumentException("요소 타입과 contentId 또는 drawingData가 올바르지 않습니다: " 
                                                   + elementRequest.getElementType());
            }
        });

        // [요소 생성] DiaryElement 객체 생성 및 저장 후 Element ID 리스트 반환
        List<String> savedElementIds = request.getElements().stream()
            .peek(elementRequest -> {
                if (!elementRequest.isValidByType()) {
                    throw new IllegalArgumentException("요소 타입과 contentId 또는 drawingData가 올바르지 않습니다: " + elementRequest.getElementType());
                }
            })
            .map(elementRequest -> {
                DiaryElement element = DiaryElement.builder()
                        .diaryId(diary.getId())
                        .elementType(elementRequest.getElementType())
                        .contentId(elementRequest.getContentId())
                        .drawingData(elementRequest.getDrawingData())
                        .position(Optional.ofNullable(elementRequest.getPosition()).orElse(List.of(0.0, 0.0)))
                        .scale(Optional.ofNullable(elementRequest.getScale()).orElse(1.0))
                        .rotation(Optional.ofNullable(elementRequest.getRotation()).orElse(0.0))
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                diaryElementRepository.save(element);
                return element.getId(); // ✅ 여기 변경!
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

        List<DiaryElement> elements = diaryElementRepository.findAllByDiaryIdAndIsDeletedFalse(diaryId);

        // diary.elementIds의 순서대로 elements 정렬
        Map<String, DiaryElement> elementMap = elements.stream()
            .collect(Collectors.toMap(DiaryElement::getId, e -> e)); // ✅ getId()로 변경

        List<DiaryElement> sortedElements = diary.getElementIds().stream()
            .map(elementMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return DiaryDetailResponse.from(diary, sortedElements);
    }
    

	 // 일기 목록 조회 (현재 그대로 유지 가능)
	 @Transactional(readOnly = true)
	 public PagedDiaryResponse getDiaries(DiarySearchRequest request, int page, int size) {
	     Criteria criteria = diaryListSearch.buildSearch(request);
	     Query query = new Query(criteria)
	         .with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
	
	     List<Diary> diaries = mongoTemplate.find(query, Diary.class);
	     long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Diary.class);
	
	     return PagedDiaryResponse.of(diaries, page, size, total);
	 }
    
    // 일기 메타 수정
    @Transactional
    public DiaryMetaUpdateResponse updateDiaryMeta(String diaryId, DiaryMetaUpdateRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 일기입니다."));

        Map<String, DiaryMetaUpdateResponse.UpdateField> updatedFields = new HashMap<>();

        if (request.getTitle() != null && !request.getTitle().equals(diary.getTitle())) {
            updatedFields.put("title", new DiaryMetaUpdateResponse.UpdateField(diary.getTitle(), request.getTitle()));
            diary.setTitle(request.getTitle());
        }

        if (request.getBackgroundContentId() != null && !request.getBackgroundContentId().equals(diary.getBackgroundContentId())) {
            updatedFields.put("backgroundContentId", new DiaryMetaUpdateResponse.UpdateField(diary.getBackgroundContentId(), request.getBackgroundContentId()));
            diary.setBackgroundContentId(request.getBackgroundContentId());
        }

        if (request.getThemeColor() != null && !request.getThemeColor().equals(diary.getThemeColor())) {
            updatedFields.put("themeColor", new DiaryMetaUpdateResponse.UpdateField(diary.getThemeColor(), request.getThemeColor()));
            diary.setThemeColor(request.getThemeColor());
        }

        if (request.getEmotionContentId() != null && !request.getEmotionContentId().equals(diary.getEmotionContentId())) {
            updatedFields.put("emotionContentId", new DiaryMetaUpdateResponse.UpdateField(diary.getEmotionContentId(), request.getEmotionContentId()));
            diary.setEmotionContentId(request.getEmotionContentId());
        }

        if (request.getIsShared() != null && !request.getIsShared().equals(diary.getIsShared())) {
            updatedFields.put("isShared", new DiaryMetaUpdateResponse.UpdateField(diary.getIsShared(), request.getIsShared()));
            diary.setIsShared(request.getIsShared());
        }

        if (request.getOpenAt() != null && !request.getOpenAt().equals(diary.getOpenAt())) {
            updatedFields.put("openAt", new DiaryMetaUpdateResponse.UpdateField(diary.getOpenAt(), request.getOpenAt()));
            diary.setOpenAt(request.getOpenAt());
        }

        if (updatedFields.isEmpty()) {
            throw new RuntimeException("수정된 내용이 없습니다.");
        }

        diary.setUpdatedAt(Instant.now());
        updatedFields.put("updatedAt", new DiaryMetaUpdateResponse.UpdateField(null, diary.getUpdatedAt()));

        diaryRepository.save(diary);

        return DiaryMetaUpdateResponse.builder()
            .diaryId(diaryId)
            .updatedFields(updatedFields)
            .build();
    }
    
    // 일기 요소 순서 변경
    @Transactional
    public DiaryElementsOrderResponse updateDiaryElements(String diaryId, DiaryElementsOrderUpdateRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

        List<String> requestedElementIds = request.getElementIds();

        if (requestedElementIds == null || requestedElementIds.isEmpty()) {
            throw new IllegalArgumentException("요소 목록이 비어있습니다.");
        }

        if (requestedElementIds.size() == 1) {
            throw new IllegalArgumentException("요소가 1개뿐이므로 순서를 변경할 수 없습니다.");
        }

        if (Objects.equals(diary.getElementIds(), requestedElementIds)) {
            throw new IllegalArgumentException("요소들의 순서가 변경되지 않았습니다.");
        }

        // Iterable을 List로 변환
        List<DiaryElement> elements = new ArrayList<>();
        diaryElementRepository.findAllById(requestedElementIds).forEach(elements::add);

        if (elements.size() != requestedElementIds.size()) {
            throw new IllegalArgumentException("요청한 요소 중 일부가 존재하지 않습니다.");
        }

        diary.setElementIds(requestedElementIds);
        diary.setUpdatedAt(Instant.now());

        diaryRepository.save(diary);

        return DiaryElementsOrderResponse.of(
            diary.getId(),
            diary.getElementIds(),
            diary.getUpdatedAt()
        );
    }

    
    // 일기 논리적 삭제
    @Transactional
    public DiaryDeleteResponse deleteDiary(String diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

        if (Boolean.TRUE.equals(diary.getIsDeleted())) {
            throw new NoSuchElementException("존재하지 않는 일기입니다.");
        }

        diary.setIsDeleted(true);
        diary.setUpdatedAt(Instant.now());

        diaryRepository.save(diary);

        return DiaryDeleteResponse.of(diary.getId(), diary.getUserId());
    }
}