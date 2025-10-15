package com.ktb.ktb_community.post.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.post.dto.request.PostCreateRequest;
import com.ktb.ktb_community.post.dto.request.PostUpdateRequest;
import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    /*
    Todo
     1. Authentication 객체
     2. Interceptor에서 User-Agent 파싱해 device type 추출 -> 페이징
     */

    // Get - post 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<CursorResponse<PostListResponse>>> getPostList(
            @RequestParam Long cursor
    ) {
        log.info("getPostList");
        /*
        device type 일단 하드코딩
         */
        int limit = getPageLimit("mobile");

        CursorResponse<PostListResponse> responses = postService.getPostList(cursor, limit);

        ApiResponse<CursorResponse<PostListResponse>> apiResponse = ApiResponse.<CursorResponse<PostListResponse>>builder()
                .success(true)
                .data(responses)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Get - post 상세 조회
//    @GetMapping("/{postId}")
//    public ResponseEntity<?> getPost(@PathVariable Long postId) {
//
//    }

    // Post - post 작성
//    @PostMapping
//    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest postCreateRequest) {
//
//    }

    // Patch - post 수정
//    @PatchMapping("/{postId}")
//    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest postUpdateRequest) {
//
//    }

    // Delete - post 삭제
//    @DeleteMapping("/{postId}")
//    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
//
//    }

    // 디바이스 타입에 따른 페이징 limit
    private int getPageLimit(String deviceType) {
        int limit = 10;
        switch (deviceType) {
            case "mobile":
                limit = 10;
                break;
            case "tablet":
                limit = 15;
                break;
            case "pc":
                limit = 20;
        }

        return limit;
    }
}
