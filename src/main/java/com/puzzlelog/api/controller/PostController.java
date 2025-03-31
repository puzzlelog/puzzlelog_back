package com.puzzlelog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.puzzlelog.api.dao.document.Comment;
import com.puzzlelog.api.dao.document.Post;
import com.puzzlelog.api.dto.request.comment.CommentRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.CommentService;
import com.puzzlelog.api.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // 게시글 업로드 API
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody Post postDocument) {
        Post newPost = postService.createPost(
        	postDocument.getUserId(),
        	postDocument.getDiaryId(),
        	postDocument.getTitle()
        );
        ApiResponse<Post> response = ApiResponse.success(newPost, "게시글이 성공적으로 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 모든 게시글 조회 API
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        ApiResponse<List<Post>> response = ApiResponse.success(posts, "모든 게시글 조회 성공");
        return ResponseEntity.ok(response);
    }

    // 좋아요 토글 API
    @PatchMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Post>> toggleLike(@PathVariable String postId, @RequestParam String userId) {
        Post updatedPost = postService.toggleLike(postId, userId);
        ApiResponse<Post> response = ApiResponse.success(updatedPost, "좋아요 상태가 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 특정 게시글 ID로 조회 API
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable String postId) {
        Post post = postService.getPostById(postId);
        ApiResponse<Post> response = ApiResponse.success(post, "게시글 조회 성공");
        return ResponseEntity.ok(response);
    }

    // 게시글에 댓글 작성 API
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Comment>> addComment(@PathVariable String postId, @RequestBody CommentRequest commentRequest) {
        Comment newComment = commentService.addComment(postId, commentRequest);
        ApiResponse<Comment> response = ApiResponse.success(newComment, "댓글이 성공적으로 작성되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 게시글의 댓글 목록 조회 API
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable String postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId, false);
        ApiResponse<List<Comment>> response = comments.isEmpty()
                ? ApiResponse.fail("댓글이 없습니다.")
                : ApiResponse.success(comments);
        return ResponseEntity.ok(response);
    }

    // 댓글 개수 반환 API
    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(@PathVariable String postId) {
        long count = commentService.getCommentCountByPostId(postId);
        ApiResponse<Long> response = ApiResponse.success(count);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제 API
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable String postId) {
        boolean success = postService.deletePost(postId);
        String message = success ? "게시글이 삭제되었습니다." : "게시글을 찾을 수 없습니다.";
        ApiResponse<String> response = success
                ? ApiResponse.successMessage(message)
                : ApiResponse.fail(message);
        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }

    // 댓글 삭제 API
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String postId, @PathVariable String commentId) {
        commentService.deleteComment(commentId);
        ApiResponse<Void> response = ApiResponse.successMessage("댓글이 삭제되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
