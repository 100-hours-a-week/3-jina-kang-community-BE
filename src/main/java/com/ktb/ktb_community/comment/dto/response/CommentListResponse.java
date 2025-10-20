package com.ktb.ktb_community.comment.dto.response;

import java.time.LocalDateTime;

public record CommentListResponse (
    Long commentId,
    String authorName,
    String content,
    LocalDateTime createdAt,
    boolean isAuthors
) {}
