package com.puzzlelog.api.repository.mongo;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.puzzlelog.api.dao.document.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostIdAndDeleted(String postId, boolean deleted);
    long countByPostIdAndDeleted(String postId, boolean deleted);
}
