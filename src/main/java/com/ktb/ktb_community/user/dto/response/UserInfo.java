package com.ktb.ktb_community.user.dto.response;

import com.ktb.ktb_community.user.entity.UserRole;

public record UserInfo(
        Long userId,
        UserRole userRole,
        String profileImageUrl
) {}
