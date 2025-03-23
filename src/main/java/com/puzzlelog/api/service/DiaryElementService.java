package com.puzzlelog.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryElement;
import com.puzzlelog.api.dao.document.ElementDecoration;
import com.puzzlelog.api.dao.document.Piece;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementRequest;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementSearchRequest;
import com.puzzlelog.api.dto.request.diary.element.DiaryElementUpdateRequest;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementDeleteResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementResponse;
import com.puzzlelog.api.dto.response.diary.element.DiaryElementUpdateResponse;
import com.puzzlelog.api.dto.response.diary.element.PagedDiaryElementResponse;
import com.puzzlelog.api.repository.mongo.DiaryElementRepository;
import com.puzzlelog.api.repository.mongo.DiaryRepository;
import com.puzzlelog.api.repository.mongo.PieceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryElementService {
	
	private final DiaryRepository diaryRepository;
	private final  DiaryElementRepository diaryElementRepository;
	private final PieceRepository pieceRepository;

	// 요소 생성
	@Transactional
	public DiaryElementResponse createDiaryElement(String diaryId, DiaryElementRequest request) {

	    Diary diary = diaryRepository.findById(diaryId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

	    if (diary.isDeleted()) {
	        throw new NoSuchElementException("존재하지 않는 일기입니다.");
	    }

	    String errorMessage = request.validateAndGetMessage();
	    if (errorMessage != null) {
	        throw new IllegalArgumentException(errorMessage);
	    }

	    DiaryElement element = DiaryElement.builder()
	        .diaryId(diaryId)
	        .elementType(request.getElementType())
	        .contentId(request.getContentId())
	        .drawingData(request.getDrawingData())
	        .date(request.getDate())
	        .position(request.getPosition())
	        .scale(request.getScale())
	        .rotation(request.getRotation())
	        .createdAt(Instant.now())
	        .updatedAt(Instant.now())
	        .deleted(false)
	        .build();

	    diaryElementRepository.save(element);

	    // 생성된 요소를 일기에도 추가
	    List<String> updatedElementIds = new ArrayList<>(diary.getElementIds());
	    updatedElementIds.add(element.getId());

	    diary.setElementIds(updatedElementIds);
	    diary.setUpdatedAt(Instant.now());
	    diaryRepository.save(diary);

	    return DiaryElementResponse.from(element);
	}
	
	// 단일 요소 조회
	@Transactional(readOnly = true)
	public DiaryElementResponse getElement(String diaryId, String elementId) {
	    Diary diary = diaryRepository.findById(diaryId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

	    if (diary.isDeleted()) {
	        throw new NoSuchElementException("존재하지 않는 일기입니다.");
	    }

	    DiaryElement element = diaryElementRepository.findByIdAndDeletedFalse(elementId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요소입니다."));

	    return DiaryElementResponse.from(element);
	}
	
	// 요소 목록 조회
	@Transactional(readOnly = true)
	public PagedDiaryElementResponse getElements(String diaryId, DiaryElementSearchRequest request) {
	    Diary diary = diaryRepository.findById(diaryId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

	    if (diary.isDeleted()) { // ✅ 수정됨
	        throw new NoSuchElementException("존재하지 않는 일기입니다.");
	    }

	    final List<String> allowedTypes = List.of("TEXT", "IMAGE", "AUDIO", "VIDEO", "STICKER", "DRAWING", "DATE");
	    if (request.getElementType() != null && !request.getElementType().isBlank()) {
	        if (!allowedTypes.contains(request.getElementType())) {
	            throw new IllegalArgumentException("잘못된 요소 타입입니다: " + request.getElementType());
	        }
	    }

	    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
	    Page<DiaryElement> elementPage;

	    if (request.getElementType() != null && !request.getElementType().isBlank()) {
	        elementPage = diaryElementRepository.findAllByDiaryIdAndElementTypeAndDeletedFalse(diaryId, request.getElementType(), pageable); // ✅ 수정됨
	    } else {
	        elementPage = diaryElementRepository.findAllByDiaryIdAndDeletedFalse(diaryId, pageable); // ✅ 수정됨
	    }

	    return PagedDiaryElementResponse.of(
	        elementPage.getContent(),
	        request.getPage(),
	        request.getSize(),
	        elementPage.getTotalElements()
	    );
	}
	
	// 요소 수정
	@Transactional
	public DiaryElementUpdateResponse updateDiaryElement(String diaryId, String elementId, DiaryElementUpdateRequest request) {
	    Diary diary = diaryRepository.findById(diaryId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

	    if (diary.isDeleted()) {
	        throw new NoSuchElementException("존재하지 않는 일기입니다.");
	    }

	    DiaryElement element = diaryElementRepository.findById(elementId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요소입니다."));

	    if (element.isDeleted()) {
	        throw new NoSuchElementException("존재하지 않는 요소입니다.");
	    }

	    Map<String, DiaryElementUpdateResponse.UpdateField> updatedFields = new HashMap<>();

	    if (request.getContentId() != null && !request.getContentId().isBlank()
	        && !request.getContentId().equals(element.getContentId())) {

	        String elementType = element.getElementType();

	        if ("DRAWING".equals(elementType)) {
	            throw new IllegalArgumentException("DRAWING 타입 요소는 contentId를 변경할 수 없습니다.");
	        }

	        Piece piece = pieceRepository.findById(request.getContentId())
	            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 콘텐츠입니다."));

	        if (!elementType.equals(piece.getType())) {
	            throw new IllegalArgumentException("요소 타입과 콘텐츠 타입이 일치하지 않습니다.");
	        }

	        updatedFields.put("contentId", new DiaryElementUpdateResponse.UpdateField(element.getContentId(), request.getContentId()));
	        element.setContentId(request.getContentId());
	    }

	    if (request.getDrawingData() != null 
	        && !request.getDrawingData().equals(element.getDrawingData())) {
	        if (!"DRAWING".equals(element.getElementType())) {
	            throw new IllegalArgumentException("DRAWING 타입이 아닌 요소는 drawingData를 지정할 수 없습니다.");
	        }

	        updatedFields.put("drawingData", new DiaryElementUpdateResponse.UpdateField(element.getDrawingData(), request.getDrawingData()));
	        element.setDrawingData(request.getDrawingData());
	    }
	    
	    if (request.getDate() != null && !request.getDate().equals(element.getDate())) {
	        if (!"DATE".equals(element.getElementType())) {
	        	throw new IllegalArgumentException("DATE 타입의 요소만 날짜를 지정할 수 있습니다.");
	        }
	        if (!request.getDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
	            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD 형식 필요)");
	        }

	        updatedFields.put("date", new DiaryElementUpdateResponse.UpdateField(element.getDate(), request.getDate()));
	        element.setDate(request.getDate());
	    }

	    if (request.getPosition() != null 
	        && !request.getPosition().equals(element.getPosition())) {
	        updatedFields.put("position", new DiaryElementUpdateResponse.UpdateField(element.getPosition(), request.getPosition()));
	        element.setPosition(request.getPosition());
	    }

	    if (request.getScale() != null 
	        && !request.getScale().equals(element.getScale())) {
	        updatedFields.put("scale", new DiaryElementUpdateResponse.UpdateField(element.getScale(), request.getScale()));
	        element.setScale(request.getScale());
	    }

	    if (request.getRotation() != null 
	        && !request.getRotation().equals(element.getRotation())) {
	        updatedFields.put("rotation", new DiaryElementUpdateResponse.UpdateField(element.getRotation(), request.getRotation()));
	        element.setRotation(request.getRotation());
	    }

	    if (request.getDecoration() != null) {
	        ElementDecoration newDecoration = ElementDecoration.from(request.getDecoration());
	        if (!newDecoration.equals(element.getDecoration())) {
	            updatedFields.put("decoration", new DiaryElementUpdateResponse.UpdateField(element.getDecoration(), newDecoration));
	            element.setDecoration(newDecoration);
	        }
	    }

	    if (updatedFields.isEmpty()) {
	        throw new IllegalArgumentException("변경된 내용이 없습니다.");
	    }

	    element.setUpdatedAt(Instant.now());
	    diaryElementRepository.save(element);

	    return DiaryElementUpdateResponse.builder()
	        .elementId(elementId)
	        .updatedFields(updatedFields)
	        .build();
	}
	
	// 요소 삭제
	@Transactional
	public DiaryElementDeleteResponse deleteDiaryElement(String diaryId, String elementId) {
	    Diary diary = diaryRepository.findById(diaryId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 일기입니다."));

	    if (diary.isDeleted()) { // ✅ 수정됨
	        throw new NoSuchElementException("존재하지 않는 일기입니다.");
	    }

	    DiaryElement element = diaryElementRepository.findById(elementId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요소입니다."));

	    if (element.isDeleted()) { // ✅ 수정됨
	        throw new NoSuchElementException("존재하지 않는 요소입니다.");
	    }

	    // 요소 논리 삭제 처리
	    element.setDeleted(true); // ✅ 수정됨
	    element.setUpdatedAt(Instant.now());
	    diaryElementRepository.save(element);

	    // 일기의 요소 목록에서도 제거
	    List<String> elementIds = diary.getElementIds().stream()
	        .filter(id -> !id.equals(elementId))
	        .collect(Collectors.toList());

	    diary.setElementIds(elementIds);
	    diary.setUpdatedAt(Instant.now());
	    diaryRepository.save(diary);

	    return DiaryElementDeleteResponse.of(diaryId, elementId);
	}

}
