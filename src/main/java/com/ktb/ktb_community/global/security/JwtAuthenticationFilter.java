package com.ktb.ktb_community.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private FilterChain filterChain;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = extractToken(request);

        // 토큰 검증 후 SecurityContext에 저장
        if(token != null && jwtProvider.validateToken(token)) {
            // 사용자 정보 추출
            Long  userId = jwtProvider.getUserIdFromToken(token);
            String userRole = jwtProvider.getUserRoleFromToken(token).toString();

            //
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(userRole)
            );

            // Authentication 객체 생성 -> 사용자 정보 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,         // 사용자 정보
                    null,           // credential
                    authorities     // 인가 정보
            );

            // SecurityContextHolder에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로
        filterChain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // "Bearer " 제거
        }

        return null;
    }
}
