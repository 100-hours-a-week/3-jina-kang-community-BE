package com.ktb.ktb_community.auth.controller;

import com.ktb.ktb_community.auth.dto.request.LoginRequest;
import com.ktb.ktb_community.auth.dto.response.LoginResponse;
import com.ktb.ktb_community.auth.dto.response.LoginResult;
import com.ktb.ktb_community.auth.service.AuthService;
import com.ktb.ktb_community.global.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                                            HttpServletResponse response
    ) {
        log.info("login - userEmail: {}", loginRequest.email());

        LoginResult result = authService.login(loginRequest);

        Cookie cookie = new Cookie("refreshToken", result.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        response.addCookie(cookie);


        LoginResponse loginResponse = new LoginResponse(
                result.userInfo(),
                result.accessToken()
        );

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(loginResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
