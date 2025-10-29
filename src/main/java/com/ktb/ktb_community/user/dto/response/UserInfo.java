package com.ktb.ktb_community.user.dto.response;

import com.ktb.ktb_community.user.entity.UserRole;
import lombok.Builder;

public record UserInfo(
        Long userId,
        UserRole userRole,
        String profileImageUrl,
        String email
) {}
