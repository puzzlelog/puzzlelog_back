package com.puzzlelog.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.puzzlelog.api.dao.document.FriendHistory;

public interface FriendHistoryRepository extends MongoRepository<FriendHistory, String> {
	
}
