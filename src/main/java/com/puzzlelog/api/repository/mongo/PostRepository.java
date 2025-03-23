package com.puzzlelog.api.repository.mongo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.puzzlelog.api.dao.document.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findById(String id);
    List<Post> findByDeletedFalse();
}