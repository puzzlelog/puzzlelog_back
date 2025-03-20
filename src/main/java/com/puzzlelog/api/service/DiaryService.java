package com.puzzlelog.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.DiaryLayer;
import com.puzzlelog.api.dto.request.diary.DiaryRequest;
import com.puzzlelog.api.dto.response.diary.DiaryResponse;
import com.puzzlelog.api.repository.mongo.DiaryLayerRepository;
import com.puzzlelog.api.repository.mongo.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryLayerRepository diaryLayerRepository;

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
            .emotion(request.getEmotion())
            .isShared(Optional.ofNullable(request.getIsShared()).orElse(false))
            .openAt(openAtInstant) // 타임캡슐이면 처리된 Instant, 일반 일기는 null
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .isDeleted(false)
            .build();

        diaryRepository.save(diary);

        // [유효성 검사] 레이어 타입에 따라 contentId/drawingData 검증
        request.getLayers().forEach(layerRequest -> {
            String pieceType = layerRequest.getPieceType();

            if ("DRAWING".equals(pieceType)) {
                // DRAWING 타입은 drawingData 필수, contentId 지정 불가
                if (layerRequest.getDrawingData() == null || layerRequest.getDrawingData().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입의 레이어는 drawingData가 필수입니다.");
                }
                if (layerRequest.getContentId() != null && !layerRequest.getContentId().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입의 레이어는 contentId를 지정할 수 없습니다.");
                }
            } else {
                // DRAWING이 아닌 타입은 contentId 필수, drawingData 지정 불가
                if (layerRequest.getContentId() == null || layerRequest.getContentId().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입이 아닌 레이어는 contentId가 필수입니다.");
                }
                if (layerRequest.getDrawingData() != null && !layerRequest.getDrawingData().isBlank()) {
                    throw new IllegalArgumentException("DRAWING 타입이 아닌 레이어는 drawingData를 지정할 수 없습니다.");
                }
            }
        });

        // [조각 생성] DiaryLayer 객체 생성 및 저장 후 Layer ID 리스트 반환
        List<String> savedLayerIds = request.getLayers().stream()
            .map(layerRequest -> {
                DiaryLayer layer = DiaryLayer.builder()
                    .diaryId(diary.getId())
                    .pieceType(layerRequest.getPieceType())
                    .contentId(layerRequest.getContentId())
                    .drawingData(layerRequest.getDrawingData())
                    .layerOrder(layerRequest.getLayerOrder())
                    .position(Optional.ofNullable(layerRequest.getPosition()).orElse(List.of(0.0, 0.0)))
                    .scale(Optional.ofNullable(layerRequest.getScale()).orElse(1.0))
                    .rotation(Optional.ofNullable(layerRequest.getRotation()).orElse(0.0))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())

                    // TODO: 추후 조각 편집 정보(PieceDecoration) 붙일 위치
                    // .decoration(layerRequest.getDecoration())

                    .build();

                diaryLayerRepository.save(layer);
                return layer.getLayerId();
            })
            .collect(Collectors.toList());

        // [Diary 업데이트] Diary에 LayerId 저장 후 갱신
        diary.setLayerIds(savedLayerIds);
        diaryRepository.save(diary);

        // [응답] DiaryResponse 생성 후 반환
        return DiaryResponse.from(diary);
    }
}