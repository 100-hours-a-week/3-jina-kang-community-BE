package com.ktb.ktb_community.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest (

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 최대 255자입니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(max = 255, message = "비밀번호는 최대 255자입니다")
    String password,

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 100, message = "닉네임은 최대 100자입니다")
    String nickname,

    @NotNull(message = "프로필이미지는 필수입니다")
    Long profileImageId
) {}
