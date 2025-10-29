package com.ktb.ktb_community.global.file.dto;

public record FileInfo(
        Long fileId,
        String fileName,
        int fileOrder,
        String fileUrl
) {}
