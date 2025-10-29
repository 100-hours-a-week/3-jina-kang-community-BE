package com.ktb.ktb_community.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.ktb_community.global.common.dto.ErrorResponse;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class SessionFilter implements Filter {

    private final RedisTemplate<String, MemberSession>  redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/auth/login",
            "/api/signup",
            "/api/signup/**"
    );

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain
    ) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        log.info("URI: {}, Method: {}", requestURI, method);

        // 쿠키 확인
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("  쿠키: {}={}", cookie.getName(), cookie.getValue());
            }
        }

        // OPTIONS 통과
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 제외 경로 체크
        if (isExcludedPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String sessionId = extractSessionId(httpRequest);

            if(sessionId == null) {
                sendErrorResponse(httpResponse, ErrorCode.UNAUTHORIZED);
                return;
            }

            String sessionKey = "SESSION-" + sessionId;

            MemberSession memberSession = redisTemplate.opsForValue().get(sessionKey);

            if(memberSession == null) {
                sendErrorResponse(httpResponse, ErrorCode.UNAUTHORIZED);
                return;
            }

            User user = userRepository.findById(memberSession.memberId())
                    .orElse(null);

            if(user == null) {
                sendErrorResponse(httpResponse, ErrorCode.UNAUTHORIZED);
                return;
            }

            log.info("인증 성공 - userId: {}", user.getId());

            httpRequest.setAttribute("loginUser", user);
            redisTemplate.expire(sessionKey, 30L, TimeUnit.MINUTES);

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("인증 처리 오류", e);
            sendErrorResponse(httpResponse, ErrorCode.INTERNAL_SERVER_ERROR);
        }

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

    private String extractSessionId(HttpServletRequest httpRequest) {
        Cookie[] cookies = httpRequest.getCookies();
        if(cookies == null) {
            return null;
        }

        return  Arrays.stream(cookies)
                .filter(cookie -> "SESSION".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private boolean isExcludedPath(String requestURI) {
        return EXCLUDED_URLS.stream()
                .anyMatch(requestURI::startsWith);
    }
}
