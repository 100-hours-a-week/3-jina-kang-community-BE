package com.ktb.ktb_community.auth.dto.response;

import com.ktb.ktb_community.user.dto.response.UserInfo;

public record LoginResult (
  UserInfo userInfo,
  String accessToken,
  String refreshToken
) {}
