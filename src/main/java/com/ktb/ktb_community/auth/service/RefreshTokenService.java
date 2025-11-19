package com.ktb.ktb_community.auth.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";

    // refresh token 조회
    public String getRefreshToken(Long userId) {
        log.info("refresh token 조회 - userId: {}", userId);

        //사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return user.getRefreshToken();
    }

    // refresh token 검증
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        log.info("refresh token 검증 - userId: {}", userId);

        String storedRefreshToken = getRefreshToken(userId);
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }

    // refresh token 삭제
    public void deleteRefreshToken(Long userId) {
        log.info("refresh token 삭제 - userId: {}", userId);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        user.deleteRefreshToken();
    }
}
