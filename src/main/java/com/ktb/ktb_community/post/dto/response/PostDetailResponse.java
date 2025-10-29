package com.ktb.ktb_community.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse (
    Long postId,
    String title,
    String authorName,
    String content,
    Long likeCount,
    Long viewCount,
    LocalDateTime createdAt,
    List<PostFileResponse> postFiles,

    boolean isAuthor
){}
