package com.puzzlelog.api.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dto.request.diary.DiaryRequest;
import com.puzzlelog.api.dto.request.diary.DiaryRequest.CropRequest;
import com.puzzlelog.api.dto.request.diary.DiaryRequest.DiaryPieceRequest;
import com.puzzlelog.api.dto.request.diary.DiaryRequest.PieceDecorationRequest;
import com.puzzlelog.api.repository.mongo.DiaryRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public DiaryService(DiaryRepository diaryRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Diary createDiary(DiaryRequest request, String authenticatedUserId) {
        // 사용자 검증
        validateUser(request.getUserId(), authenticatedUserId);

        // DiaryPiece 검증 및 생성
        List<Diary.DiaryPiece> pieces = request.getPieces().stream()
                .map(this::validateAndCreateDiaryPiece)
                .collect(Collectors.toList());

        Diary diary = Diary.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .type(request.getType())
                .pieces(pieces)
                .themeColor(request.getThemeColor())
                .isShared(request.getIsShared() != null ? request.getIsShared() : false)
                .isDeleted(false)
                .createdAt(Instant.now())
                .openAt(request.getOpenAt())
                .build();

        return diaryRepository.save(diary);
    }

    private void validateUser(String userId, String authenticatedUserId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new RuntimeException("본인만 일기를 작성할 수 있습니다.");
        }

        userRepository.findByUserId(userId).orElseThrow(() -> 
            new RuntimeException("존재하지 않는 사용자입니다.")
        );
    }

    private Diary.DiaryPiece validateAndCreateDiaryPiece(DiaryPieceRequest request) {
        validateDecoration(request.getPieceType(), request.getDecoration());

        return Diary.DiaryPiece.builder()
                .pieceId(request.getPieceId())
                .pieceType(request.getPieceType())
                .decoration(convertToEntity(request.getDecoration()))
                .build();
    }

    private void validateDecoration(Diary.PieceType type, PieceDecorationRequest decoration) {
        switch (type) {
            case TEXT:
                if (decoration.getTextStyle() == null) {
                    throw new IllegalArgumentException("TEXT 스타일이 누락되었습니다.");
                }
                decoration.setImageStyle(null);
                decoration.setVideoStyle(null);
                decoration.setAudioStyle(null);
                break;
            case IMAGE:
                if (decoration.getImageStyle() == null) {
                    throw new IllegalArgumentException("IMAGE 스타일이 누락되었습니다.");
                }
                decoration.setTextStyle(null);
                decoration.setVideoStyle(null);
                decoration.setAudioStyle(null);
                break;
            case VIDEO:
                if (decoration.getVideoStyle() == null) {
                    throw new IllegalArgumentException("VIDEO 스타일이 누락되었습니다.");
                }
                decoration.setTextStyle(null);
                decoration.setImageStyle(null);
                decoration.setAudioStyle(null);
                break;
            case AUDIO:
                if (decoration.getAudioStyle() == null) {
                    throw new IllegalArgumentException("AUDIO 스타일이 누락되었습니다.");
                }
                decoration.setTextStyle(null);
                decoration.setImageStyle(null);
                decoration.setVideoStyle(null);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 Piece 타입입니다.");
        }
    }

    private Diary.PieceDecoration convertToEntity(PieceDecorationRequest decorationRequest) {
        return Diary.PieceDecoration.builder()
                .position(new Diary.Position(decorationRequest.getPosition().getX(), decorationRequest.getPosition().getY()))
                .scale(decorationRequest.getScale())
                .rotation(new Diary.Rotation(decorationRequest.getRotation().getAngle()))
                .textStyle(decorationRequest.getTextStyle() != null ? Diary.TextStyle.builder()
                        .font(decorationRequest.getTextStyle().getFont())
                        .fontSize(decorationRequest.getTextStyle().getFontSize())
                        .color(decorationRequest.getTextStyle().getColor())
                        .bold(decorationRequest.getTextStyle().getBold())
                        .italic(decorationRequest.getTextStyle().getItalic())
                        .align(decorationRequest.getTextStyle().getAlign())
                        .build() : null)
                .imageStyle(decorationRequest.getImageStyle() != null ? Diary.ImageStyle.builder()
                        .crop(convertCrop(decorationRequest.getImageStyle().getCrop()))
                        .effects(decorationRequest.getImageStyle().getEffects())
                        .borderColor(decorationRequest.getImageStyle().getBorderColor())
                        .opacity(decorationRequest.getImageStyle().getOpacity())
                        .build() : null)
                // VideoStyle, AudioStyle도 동일한 방법으로 추가
                .build();
    }

    private Diary.Crop convertCrop(CropRequest cropRequest) {
        if (cropRequest == null) return null;

        return Diary.Crop.builder()
                .width(cropRequest.getWidth())
                .height(cropRequest.getHeight())
                .x(cropRequest.getX())
                .y(cropRequest.getY())
                .gravity(cropRequest.getGravity())
                .build();
    }
}
