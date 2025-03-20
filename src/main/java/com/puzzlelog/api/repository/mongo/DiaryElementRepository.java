package com.puzzlelog.api.repository.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.puzzlelog.api.dao.document.DiaryElement;

@Repository
public interface DiaryElementRepository extends MongoRepository<DiaryElement, ObjectId> {
	List<DiaryElement> findAllByDiaryIdOrderByElementOrderAsc(String diaryId);
}
