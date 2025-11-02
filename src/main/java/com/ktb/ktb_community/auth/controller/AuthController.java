package com.ktb.ktb_community.auth.controller;

import com.ktb.ktb_community.auth.dto.request.LoginRequest;
import com.ktb.ktb_community.auth.dto.response.LoginResponse;
import com.ktb.ktb_community.auth.dto.response.LoginResult;
import com.ktb.ktb_community.auth.dto.response.TokenResponse;
import com.ktb.ktb_community.auth.service.AuthService;
import com.ktb.ktb_community.global.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    // 로그인
    @PostMapping("/login")
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

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletResponse response,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("logout - userId: {}", userId);

        authService.logout(userId);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // access token 재발급
    @PostMapping("/reissue/access")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueAccessToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        log.info("reissueAccessToken - refreshToken: {}", refreshToken);

        TokenResponse tokenResponse = authService.reissueAccessToken(refreshToken);
        ApiResponse<TokenResponse> apiResponse = ApiResponse.success(tokenResponse);

        return ResponseEntity.ok(apiResponse);
    }


}
