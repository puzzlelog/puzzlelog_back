package com.puzzlelog.api.repository.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.puzzlelog.api.dao.document.DiaryLayer;

@Repository
public interface DiaryPieceRepository extends MongoRepository<DiaryLayer, ObjectId> {
	
}
