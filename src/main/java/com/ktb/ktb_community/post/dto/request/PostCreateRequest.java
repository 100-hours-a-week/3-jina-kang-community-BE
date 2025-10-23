package com.ktb.ktb_community.post.dto.request;

import com.ktb.ktb_community.global.file.dto.FileInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateRequest (

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 최대 255자입니다")
    String title,

    String content,

    List<PostFileRequest> postImages
){}
