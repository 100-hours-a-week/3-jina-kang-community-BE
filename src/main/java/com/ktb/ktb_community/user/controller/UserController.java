package com.ktb.ktb_community.user.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
}
