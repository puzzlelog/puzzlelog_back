package com.puzzlelog.api.repository.mongo;



import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.puzzlelog.api.dao.document.Album;

public interface AlbumRepository extends MongoRepository<Album, String> {
    List<Album> findByUserId(String userId); // 특정 사용자의 앨범만 조회

    // ✅ ObjectId를 사용하여 앨범 조회
    Optional<Album> findById(ObjectId id);
    
    // ✅ isDeleted가 false인 앨범만 가져오기
    List<Album> findByUserIdAndDeletedFalse(String userId);
}

