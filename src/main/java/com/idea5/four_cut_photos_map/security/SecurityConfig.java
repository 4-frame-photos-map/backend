package com.idea5.four_cut_photos_map.security;

import com.idea5.four_cut_photos_map.security.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                .cors().disable() // 타 도메인에서 API 호출 가능
                .csrf().disable() // CSRF 토큰 끄기
                .httpBasic().disable() // Spring Security 가 제공하는 기본 로그인 화면 사용X
                .formLogin().disable() // 폼 로그인 방식 사용X
                // jwt 사용을 위해 세션 설정해제
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                )
                // UsernamePasswordAuthenticationFilter 앞에 jwtAuthorizationFilter 추가(jwtAuthorizationFilter 먼저 실행)
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
