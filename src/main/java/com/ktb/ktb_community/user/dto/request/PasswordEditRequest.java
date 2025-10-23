package com.ktb.ktb_community.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordEditRequest(

        @NotBlank(message = "비밀번호 입력은 필수입니다")
        String password
) {}