package com.ktb.ktb_community.global.file.dto.response;

public record UploadFileResponse(
        String fileName,
        String fileUrl,
        String contentType
) {}
