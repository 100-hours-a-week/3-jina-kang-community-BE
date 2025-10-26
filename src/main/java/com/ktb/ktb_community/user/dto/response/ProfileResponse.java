package com.ktb.ktb_community.user.dto.response;

import lombok.Builder;

@Builder
public record ProfileResponse(
        String profileImageUrl,
        String nickname,
        String email
){}
