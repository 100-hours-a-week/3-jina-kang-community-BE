package com.ktb.ktb_community.post.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.post.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/postLike")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService  postLikeService;

    // Post - 좋아요
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> postLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("post like id={}", postId);

        postLikeService.postLike(postId, userId);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        return ResponseEntity.ok(apiResponse);
    }

    // Delete - 좋아요 취소
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deletePostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("delete post like id={}", postId);

        postLikeService.deletePostLike(postId, userId);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        return ResponseEntity.ok(apiResponse);
    }
}
