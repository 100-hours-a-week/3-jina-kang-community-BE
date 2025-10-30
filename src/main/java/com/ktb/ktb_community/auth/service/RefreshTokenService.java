package com.ktb.ktb_community.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";

    // refresh token redis에 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        log.info("refresh token 저장 - userId: {}", userId);

        String key = createKey(userId);
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
                );
    }

    // refresh token 조회
    public String getRefreshToken(Long userId) {
        log.info("refresh token 조회 - userId: {}", userId);

        String key = createKey(userId);
        String token = redisTemplate.opsForValue().get(key);

        return token;
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

        String key = createKey(userId);
        redisTemplate.delete(key);
    }

    private String createKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }
}
