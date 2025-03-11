package com.puzzlelog.puzzlelog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.puzzlelog.puzzlelog.document.ChallengeDocument;

public interface ChallengeRepository extends MongoRepository<ChallengeDocument, String> {
    List<ChallengeDocument> findByIsActiveTrue(); // 활성화된 챌린지 조회
}

