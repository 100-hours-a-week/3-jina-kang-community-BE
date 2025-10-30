package com.ktb.ktb_community.user.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.security.LoginUser;
import com.ktb.ktb_community.user.dto.request.PasswordCheckRequest;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.ProfileResponse;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
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

    // 닉네임 중복 확인 - 회원가입
    @GetMapping("/signup/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(
            @RequestParam(required = true) String nickname
    ) {
        log.info("닉네임 중복확인 - 회원정보 변경시");

        boolean response = userService.isNicknameExisted(nickname);

        ApiResponse<Boolean> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    // 이메일 중복 확인 - 회원가입
    @GetMapping("/signup/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @RequestParam(required = true) String email
    ) {
        log.info("이메일 중복확인 - 회원정보 변경시");

        boolean response = userService.isEmailExisted(email);

        ApiResponse<Boolean> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    // 회원 정보 조회
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @LoginUser User user
    ) {
        log.info("회원 정보 조회");

        ProfileResponse response = userService.getProfile(user.getId());
        ApiResponse<ProfileResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 회원정보 수정
    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> editProfile(
            @RequestBody ProfileEditRequest request,
            @LoginUser User user
    ) {
        log.info("회원정보 변경");

        ProfileResponse response = userService.editProfile(request, user.getId());

        ApiResponse<ProfileResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 확인
    @PostMapping("/users/me/password")
    public ResponseEntity<ApiResponse<Boolean>> checkPassword(
            @RequestBody PasswordCheckRequest request,
            @LoginUser User user
    ) {
        log.info("기존 비밀번호 확인");

        boolean result = userService.checkPassword(request, user.getId());
        ApiResponse<Boolean> apiResponse = ApiResponse.success(result);
        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 수정
    @PatchMapping("/users/me/password")
    public ResponseEntity<ApiResponse<Void>> editPassword(
            @RequestBody PasswordEditRequest request,
            @LoginUser User user
    ) {
        log.info("비밀번호 수정");

        userService.editPassword(request, user.getId());

        ApiResponse<Void> apiResponse = ApiResponse.success(null);

        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 중복확인 - 회원정보 수정
    @GetMapping("/users/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(
            @RequestParam(required = true) String nickname,
            @LoginUser User user
    ) {
        log.info("닉네임 중복확인 - 회원정보 변경시");

        boolean response = userService.isNicknameDuplicated(nickname, user.getId());

        ApiResponse<Boolean> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }


}
