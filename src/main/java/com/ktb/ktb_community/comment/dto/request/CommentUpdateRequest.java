package com.ktb.ktb_community.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest (

        @NotBlank(message = "댓글을 입력해주세요")
        @Size(max = 500, message = "댓글은 최대 500자입니다")
        String content
) {}