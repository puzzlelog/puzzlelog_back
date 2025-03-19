package com.puzzlelog.api.service;

import org.springframework.stereotype.Service;

import com.puzzlelog.api.repository.mongo.DiaryPieceRepository;
import com.puzzlelog.api.repository.mongo.DiaryRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryPieceRepository diaryPieceRepository;
    private final UserRepository userRepository;

    public DiaryService(DiaryRepository diaryRepository, DiaryPieceRepository diaryPieceRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.diaryPieceRepository = diaryPieceRepository;
        this.userRepository = userRepository;
    }
}
