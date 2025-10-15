package com.ktb.ktb_community.global.security;

import com.ktb.ktb_community.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private Key secretKey;

    @PostConstruct
    public void init() {
        // Base64 디코딩하여 Key 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // access token 생성
    public String createAccessToken(Long userId, UserRole role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        String accessToken = Jwts.builder()
                .claim("id", userId)
                .claim("role", role.name())
                .claim("tokenType", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // refresh token 생성
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        String refreshToken = Jwts.builder()
                .claim("id", userId)
                .claim("tokenType", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }

    // token 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰: {}", token);
            return false;
        } catch (JwtException e) {
            return false;
        }
    }

    // token에서 claims 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // user id 추출 -> Authentication
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("id",  Long.class);
    }

    // user role 추출 -> Authentication
    public UserRole getUserRoleFromToken(String token) {
        Claims claims = getClaims(token);
        String role = claims.get("role", String.class);
        return UserRole.valueOf(role);
    }

    // 토큰 타입 추출
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("tokenType", String.class);
    }

    // access token인지 확인
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return tokenType.equalsIgnoreCase("ACCESS");
        } catch (Exception e) {
            return false;
        }
    }

    // refresh token인지 확인
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return tokenType.equalsIgnoreCase("REFRESH");
        }  catch (Exception e) {
            return false;
        }
    }

}
