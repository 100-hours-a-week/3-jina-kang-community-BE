package com.ktb.ktb_community.post.dto.response;

import java.time.LocalDateTime;

public record PostDetailResponse (
    Long PostId,
    String title,
    String authorName,
    String content,
    Long likeCount,
    Long viewCount,
    LocalDateTime createdAt
){}
