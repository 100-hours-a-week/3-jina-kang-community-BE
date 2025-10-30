package com.ktb.ktb_community.post.dto.response;

public record PostFileResponse (
        Long postFileId,
        String fileName,
        String url,
        int imageIndex,
        String contentType
) {}