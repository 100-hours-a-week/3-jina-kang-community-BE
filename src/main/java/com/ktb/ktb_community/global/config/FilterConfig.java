package com.ktb.ktb_community.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb.ktb_community.global.security.CorsFilter;
import com.ktb.ktb_community.global.security.JwtAuthenticationFilter;
import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.user.repository.UserRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public FilterConfig(
            JwtProvider jwtProvider,
            ObjectMapper objectMapper,
            UserRepository userRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    // CORS 필터 등록 - 1순위
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(new CorsFilter());
        registrationBean.addUrlPatterns("/api/*");  // API 경로에만 적용
        registrationBean.setOrder(1);
        registrationBean.setName("corsFilter");

        return registrationBean;
    }


     // JWT 인증 필터 등록 - 2순위
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean =
                new FilterRegistrationBean<>();

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtProvider,
                objectMapper,
                userRepository
        );

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*");  // API 경로에만 적용
        registrationBean.setOrder(2);
        registrationBean.setName("jwtAuthenticationFilter");

        return registrationBean;
    }
}
