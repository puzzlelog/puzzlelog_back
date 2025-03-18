package com.puzzlelog.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.puzzlelog.api.dao.document.Diary;
import org.bson.types.ObjectId;

public interface DiaryRepository extends MongoRepository<Diary, ObjectId> {
	
}
