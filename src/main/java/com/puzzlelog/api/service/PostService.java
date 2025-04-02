package com.puzzlelog.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.puzzlelog.api.dao.document.Post;
import com.puzzlelog.api.repository.mongo.DiaryRepository;
import com.puzzlelog.api.repository.mongo.PostRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private DiaryRepository diaryRepository;

	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	// 게시글 작성 기능
	public Post createPost(String userId, String diaryId, String title) {
	    Post post = Post.builder()
	        .userId(userId)
	        .diaryId(diaryId)
	        .title(title)
	        .content("")
	        .createdAt(LocalDateTime.now())
	        .likesCount(0)
	        .liked(false)
	        .deleted(false)
	        .build();
	    
	    return postRepository.save(post);
	}

	// 모든 게시글 조회 (삭제되지 않은 게시글만)
	public List<Post> getAllPosts() {
	    return postRepository.findByDeletedFalse();
	}

    public Post toggleLike(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        if (post.isLiked()) {
            post.setLiked(false);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            post.setLiked(true);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        return postRepository.save(post);
    }

	// 특정 게시글 ID로 조회하는 기능
	public Post getPostById(String id) {
		return postRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다.")); // 게시글을 찾을 수 없음
	}
	
	// 게시글 삭제 (isDeleted = true로 설정)
    public boolean deletePost(String postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setDeleted(true);
            postRepository.save(post);
            return true;
        }
        return false;
    }
}
