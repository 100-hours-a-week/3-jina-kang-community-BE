package com.ktb.ktb_community.comment.controller;

import com.ktb.ktb_community.comment.dto.request.CommentCreateRequest;
import com.ktb.ktb_community.comment.dto.request.CommentUpdateRequest;
import com.ktb.ktb_community.comment.dto.response.CommentResponse;
import com.ktb.ktb_community.comment.service.CommentService;
import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<CursorResponse<CommentResponse>>> getCommentList(
            @RequestParam(required = false) Long cursor,
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("getCommentList");

        String deviceType = "mobile";

        CursorResponse<CommentResponse> response = commentService.getCommentList(cursor, deviceType, postId, userId);

        ApiResponse<CursorResponse<CommentResponse>> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 댓글 작성
    @PostMapping()
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("createComment");

        CommentResponse response = commentService.createComment(request, postId, userId);

        ApiResponse<CommentResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("updateComment");

        CommentResponse response = commentService.updateComment(request, commentId, userId);

        ApiResponse<CommentResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("deleteComment");

        commentService.deleteComment(commentId, userId);

        return ResponseEntity.noContent().build();
    }
}
