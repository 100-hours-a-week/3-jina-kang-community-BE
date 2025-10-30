package com.ktb.ktb_community.user.dto.request;

public record ProfileImageRequest(
        String fileName,
        String fileUrl,
        String contentType
) {}
