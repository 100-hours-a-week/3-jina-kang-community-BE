package com.ktb.ktb_community.post.dto.request;

public record PostFileRequest (
        String fileName,
        int fileOrder,
        String fileKey,      // S3 파일 키
        String s3Url,        // S3 URL
        String contentType
){}
