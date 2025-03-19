package com.puzzlelog.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.puzzlelog.api.dao.document.Comment;
import com.puzzlelog.api.dao.document.Post;
import com.puzzlelog.api.dto.request.comment.CommentRequest;
import com.puzzlelog.api.repository.mongo.CommentRepository;
import com.puzzlelog.api.repository.mongo.PostRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // 댓글 작성
    public Comment addComment(String postId, CommentRequest commentRequest) {
        // 게시글이 존재하는지 확인 (MongoDB에서 postId는 String 타입으로 저장)
        Post post = postRepository.findById(postId)
        	.orElseThrow(() -> new NoSuchElementException("Post not found"));

        // 댓글 객체 생성
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(commentRequest.getUserId());
        comment.setContent(commentRequest.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDeleted(false);

        // 댓글 저장
        return commentRepository.save(comment);
    }
    
    public List<Comment> getCommentsByPostId(String postId, boolean isDeleted) {
        return commentRepository.findByPostIdAndIsDeleted(postId, isDeleted);
    }
    
    // 특정 게시글의 댓글 수 조회
    public long getCommentCountByPostId(String postId) {
    	return commentRepository.countByPostIdAndIsDeleted(postId, false);
    }
    
    // 댓글 삭제 (isDeleted 필드를 true로 업데이트)
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        comment.setDeleted(true);  // 댓글 삭제 표시
        commentRepository.save(comment);
    }
}
