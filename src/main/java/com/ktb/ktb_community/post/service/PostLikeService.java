package com.ktb.ktb_community.post.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.entity.PostLike;
import com.ktb.ktb_community.post.repository.PostLikeRepository;
import com.ktb.ktb_community.post.repository.PostRepository;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // post like
    @Transactional
    public void postLike(Long postId, Long userId) {
        log.info("post like - {}", postId);

        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        // 이미 좋아요를 눌렀는지 확인
        if(postLikeRepository.existsByPostIdAndUserId(userId, postId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        }
        // 좋아요
        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        postLikeRepository.save(postLike);

        // 좋아요 수 증가
        post.getPostStatus().incrementLikeCount();
    }

    // delete post like
    @Transactional
    public void deletePostLike(Long postId, Long userId) {
        log.info("delete post like - {}", postId);

        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        // 좋아요를 한 글인지 확인
        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));
        // 좋아요 취소
        postLikeRepository.delete(postLike);
        // 좋아요 수 감소
        post.getPostStatus().decrementLikeCount();
    }
}
