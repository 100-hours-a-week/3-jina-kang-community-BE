package com.ktb.ktb_community.post.dto.response;

import java.time.LocalDateTime;

public record PostListResponse (
        Long postId,
        String title,
        String authorName,
        Long likeCount,
        Long commentCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
