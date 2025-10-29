package com.ktb.ktb_community.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordCheckRequest(
        @NotBlank(message = "이전 비밀번호 입력은 필수입니다")
        @Size(max = 50, message = "비밀번호는 최대 50자리 입니다")
        String oldPassword
) {}
