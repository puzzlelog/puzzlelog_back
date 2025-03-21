package com.puzzlelog.api.repository.mongo;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.puzzlelog.api.dao.document.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
	List<Comment> findByPostIdAndIsDeleted(String postId, boolean isDeleted);
    long countByPostIdAndIsDeleted(String postId, boolean isDeleted);
}
