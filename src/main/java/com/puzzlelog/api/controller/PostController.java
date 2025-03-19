package com.puzzlelog.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dao.document.Comment;
import com.puzzlelog.api.dao.document.Post;
import com.puzzlelog.api.dto.request.comment.CommentRequest;
import com.puzzlelog.api.service.CommentService;
import com.puzzlelog.api.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // 게시글 업로드 API (RequestBody로 받음)
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post postDocument) {
        Post newPost = postService.createPost(postDocument.getUserId(), postDocument.getContent(), postDocument.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost); // 201 Created 상태 코드 반환
    }

    // 모든 게시글 조회 API
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 좋아요 토글 API
    @PatchMapping("/{postId}/like")
    public ResponseEntity<Post> toggleLike(
            @PathVariable String postId,
            @RequestParam String userId) { // userId를 쿼리 파라미터로 받음
        Post updatedPost = postService.toggleLike(postId, userId);
        return ResponseEntity.ok(updatedPost);
    }

    // 특정 게시글 ID로 조회 API
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post); // 200 OK 반환
    }

    // 게시글에 댓글 작성 API
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable String postId,
            @RequestBody CommentRequest commentRequest) {
        
        Comment newComment = commentService.addComment(postId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    // 특정 게시글의 댓글 목록 조회 API
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId, false);
        
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        
        return ResponseEntity.ok(comments); // 200 OK
    }

    // 댓글 개수 반환 API
    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable String postId) {
        long count = commentService.getCommentCountByPostId(postId);
        return ResponseEntity.ok(count);
    }

    // 게시글 삭제 API
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable String postId) {
        boolean success = postService.deletePost(postId);
        if (success) {
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        }
    }

    // 댓글 삭제 API
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId, @PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 삭제 성공 시 HTTP 204 반환
    }
}
