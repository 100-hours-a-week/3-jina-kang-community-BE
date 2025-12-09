package com.ktb.ktb_community.user.dto.request;

public record ProfileImageRequest(
        String fileName,
        String fileKey,      // S3 파일 키
        String s3Url,        // S3 URL
        String contentType
) {}
