package com.ktb.ktb_community.user.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.ProfileResponse;
import com.ktb.ktb_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> singup(
            @RequestBody SignupRequest request
    ) {
        log.info("회원가입");

        userService.signup(request);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        log.info("회원 정보 조회");

        ProfileResponse response = userService.getProfile(userId);
        ApiResponse<ProfileResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 회원정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> editProfile(
            @RequestBody ProfileEditRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("회원정보 변경");

        ProfileResponse response = userService.editProfile(request, userId);

        ApiResponse<ProfileResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> editPassword(
            @RequestBody PasswordEditRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("비밀번호 수정");

        userService.editPassword(request, userId);

        ApiResponse<Void> apiResponse = ApiResponse.success(null);

        return ResponseEntity.ok(apiResponse);
    }

    // 이메일 중복확인 / 닉네임 중복확인
}
