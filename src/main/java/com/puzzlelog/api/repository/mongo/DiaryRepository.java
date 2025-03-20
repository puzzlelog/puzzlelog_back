package com.puzzlelog.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.puzzlelog.api.dao.document.Diary;

public interface DiaryRepository extends MongoRepository<Diary, String> {
	
}
