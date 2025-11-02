package com.ktb.ktb_community.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileEditRequest (
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(max = 100, message = "닉네임은 최대 100자입니다")
        String nickname,

        ProfileImageRequest profileImage
){}
