package com.ktb.ktb_community.post.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.post.dto.request.PostCreateRequest;
import com.ktb.ktb_community.post.dto.request.PostUpdateRequest;
import com.ktb.ktb_community.post.dto.response.PostDetailResponse;
import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /*
    Todo
     1. Authentication 객체 - 해결
     2. Interceptor에서 User-Agent 파싱해 device type 추출 -> 페이징
     */

    // Get - post 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<CursorResponse<PostListResponse>>> getPostList(
            @RequestParam Long cursor
    ) {
        log.info("getPostList");
        /*
        TODO
            device type 일단 하드코딩
         */
        String deviceType = "mobile";

        CursorResponse<PostListResponse> responses = postService.getPostList(cursor, deviceType);

        ApiResponse<CursorResponse<PostListResponse>> apiResponse = ApiResponse.success(responses);

        return ResponseEntity.ok(apiResponse);
    }

    // Get - post 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity< ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        log.info("Post 상세 조회");

        PostDetailResponse response = postService.getPostDetail(postId, userId);

        ApiResponse<PostDetailResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // Post - post 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Post 작성 - userId: {}", userId);

        PostDetailResponse response = postService.createPost(request, userId);

        ApiResponse<PostDetailResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // Patch - post 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        log.info("Post 수정 - postId: {}", postId);

        PostDetailResponse response = postService.updatePost(postId, request, userId);

        ApiResponse<PostDetailResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // Delete - post 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Post 삭제 -  postId: {}", postId);

        postService.deletePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}
