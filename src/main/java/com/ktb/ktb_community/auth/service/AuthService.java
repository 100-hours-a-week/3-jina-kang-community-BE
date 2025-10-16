package com.ktb.ktb_community.auth.service;

import com.ktb.ktb_community.auth.dto.request.LoginRequest;
import com.ktb.ktb_community.auth.dto.response.LoginResult;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.user.dto.response.UserInfo;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /*
    TODO
        1. 로그인
        2. 로그아웃
        3. Access Token 재발급
     */

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;
    private final RefreshTokenService  refreshTokenService;

    // 로그인
    public LoginResult login(LoginRequest loginRequest) {
        log.info("login - userEmail: {}", loginRequest.email());

        // 사용자 조회 - 이메일 확인
        User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequest.email())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHRIZED_USER));

        // 비밀번호 검증
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHRIZED_USER);
        }

        // 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        // refresh token redis에 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        // UserInfo
        UserInfo userInfo = userMapper.toUserInfo(user);

        return new LoginResult(userInfo, accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(Long userId) {
        log.info("logout");

        // refreshToken 삭제
        refreshTokenService.deleteRefreshToken(userId);
    }
}
