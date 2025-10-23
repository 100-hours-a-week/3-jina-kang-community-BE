package com.ktb.ktb_community.post.dto.request;

public record PostFileRequest (
        String fileName,
        int fileOrder,
        String fileUrl
){}
