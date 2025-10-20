package com.ktb.ktb_community.comment.controller;

import com.ktb.ktb_community.comment.dto.response.CommentListResponse;
import com.ktb.ktb_community.comment.service.CommentService;
import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<CursorResponse<CommentListResponse>> getCommentList(
            @RequestParam(required = false) Long cursor,
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("getCommentList");

        String deviceType = "mobile";

        CursorResponse<CommentListResponse> response = commentService.getCommentList(cursor, deviceType, postId, userId);

        ApiResponse<CursorResponse<CommentListResponse>> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(response);
    }

    /*
    TODO
       - 댓글 수정
       - 댓글 삭제
     */
}
