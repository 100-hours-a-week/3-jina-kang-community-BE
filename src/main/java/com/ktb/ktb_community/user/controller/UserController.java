package com.ktb.ktb_community.user.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.ProfileEditResponse;
import com.ktb.ktb_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // 회원정보 수정
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<ProfileEditResponse>> editProfile(
            @RequestBody ProfileEditRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("회원정보 변경");

        ProfileEditResponse response = userService.editProfile(request, userId);

        ApiResponse<ProfileEditResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 수정
    @PostMapping("/me/password")
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
