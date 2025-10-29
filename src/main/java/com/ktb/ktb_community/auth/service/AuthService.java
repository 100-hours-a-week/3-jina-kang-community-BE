package com.ktb.ktb_community.auth.service;

import com.ktb.ktb_community.auth.dto.request.LoginRequest;
import com.ktb.ktb_community.auth.dto.response.LoginResponse;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.security.MemberSession;
import com.ktb.ktb_community.user.dto.response.UserInfo;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, MemberSession> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("login - userEmail: {}", loginRequest.email());

        // 사용자 조회 - 이메일 확인
        User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequest.email())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        // 비밀번호 검증
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        String sessionId = UUID.randomUUID().toString();
        String sessionKey = "SESSION-" + sessionId;
        redisTemplate.opsForValue().set(sessionKey, new MemberSession(user.getId()), 30L, TimeUnit.MINUTES);

        Cookie cookie = new Cookie("SESSION", sessionId);
        cookie.setMaxAge(30 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        cookie.setDomain("localhost");

        UserInfo userInfo = userMapper.toUserInfo(user);

        LoginResponse loginResponse = LoginResponse.builder()
                .userInfo(userInfo)
                .cookie(cookie)
                .build();
        return loginResponse;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("SESSION".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    String sessionKey = "SESSION-" + sessionId;
                    // 레디스에서 세션 삭제
                    redisTemplate.delete(sessionKey);
                    // 쿠키 삭제
                    Cookie deleteCookie = new Cookie("SESSION", null);
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath("/");
                    deleteCookie.setHttpOnly(true);
                    response.addCookie(deleteCookie);

                    break;
                }
            }
        }
    }
}
