package com.puzzlelog.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.puzzlelog.api.dao.document.Diary;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiaryRepository extends MongoRepository<Diary, String> {
	
	// participants에 특정 사용자가 포함된 일기 조회
    Page<Diary> findByParticipantsContaining(String userId, Pageable pageable);

    // createdAt 범위로 일기 조회
    List<Diary> findByCreatedAtBetween(Instant start, Instant end);
	
}
