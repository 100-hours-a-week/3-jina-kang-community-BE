package com.ktb.ktb_community.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.ktb_community.global.common.dto.ErrorResponse;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/auth/login",
            "/api/signup",
            "/api/signup/**",
            "/api/file/**"
    );



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 인증 필요없는 경로면 통과
        if(isExcludedPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 토큰 추출
            String token = extractToken(request);
            // 토큰 없으면 예외처리
            if(token == null) {
                sendErrorResponse(response, ErrorCode.UNAUTHORIZED);
                return;
            }

            if(jwtProvider.validateToken(token)) {
                // 사용자 정보 추출
                Long  userId = jwtProvider.getUserIdFromToken(token);
                String userRole = jwtProvider.getUserRoleFromToken(token).toString();

                // argument resolver에 user 정보 저장
                User user = userRepository.findById(userId)
                        .orElse(null);

                if(user == null) {
                    sendErrorResponse(response, ErrorCode.UNAUTHORIZED);
                    return;
                }

                log.info("인증 성공 - userId: {}", user.getId());

                request.setAttribute("loginUser", user);
            }
            else {
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
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

    private boolean isExcludedPath(String requestURI) {
        return EXCLUDED_URLS.stream()
                .anyMatch(requestURI::startsWith);
    }
}
