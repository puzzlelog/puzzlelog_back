package com.puzzlelog.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.puzzlelog.api.dao.document.UserHistory;

public interface UserHistoryRepository extends MongoRepository<UserHistory, String> {
    
}
