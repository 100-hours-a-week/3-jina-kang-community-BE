package com.ktb.ktb_community.user.dto.response;

import lombok.Builder;

@Builder
public record ProfileResponse(
        ProfileImageResponse profileImage,
        String nickname,
        String email
){}
