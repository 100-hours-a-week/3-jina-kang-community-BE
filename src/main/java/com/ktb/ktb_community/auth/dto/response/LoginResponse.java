package com.ktb.ktb_community.auth.dto.response;

import com.ktb.ktb_community.user.dto.response.UserInfo;
import jakarta.servlet.http.Cookie;
import lombok.Builder;

@Builder
public record LoginResponse (
        UserInfo userInfo,
        Cookie cookie
) {}
