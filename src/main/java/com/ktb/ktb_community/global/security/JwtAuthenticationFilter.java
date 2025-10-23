package com.ktb.ktb_community.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.ktb_community.global.common.dto.ErrorResponse;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        try {
            // 헤더에서 토큰 추출
            String token = extractToken(request);

            // 토큰 검증 후 SecurityContext에 저장
            if(token == null) {
                chain.doFilter(request, response);
                return;
            }

            if(jwtProvider.validateToken(token)) {
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

            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Access Token 만료
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);

        } catch (MalformedJwtException | SignatureException e) {
            // 잘못된 토큰
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);

        } catch (Exception e) {
            // 기타 에러
            sendErrorResponse(response, ErrorCode.UNAUTHORIZED);
        }
    }

    // 헤더에서 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // "Bearer " 제거
        }

        return null;
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
