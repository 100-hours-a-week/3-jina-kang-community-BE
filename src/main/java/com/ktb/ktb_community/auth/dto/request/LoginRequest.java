package com.ktb.ktb_community.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "이메일 입력은 필수입니다")
        @Email(message = "옳바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호 입력은 필수입니다")
        String password
){}
