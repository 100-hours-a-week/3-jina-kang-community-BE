package com.ktb.ktb_community.auth.controller;

import com.ktb.ktb_community.auth.dto.request.LoginRequest;
import com.ktb.ktb_community.auth.dto.response.LoginResponse;
import com.ktb.ktb_community.auth.service.AuthService;
import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.security.LoginUser;
import com.ktb.ktb_community.user.dto.response.UserInfo;
import com.ktb.ktb_community.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfo>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse result = authService.login(loginRequest);
        Cookie cookie = result.cookie();
        response.addCookie(cookie);

        UserInfo userInfo = result.userInfo();
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        log.info("logout");

        authService.logout(request, response);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
