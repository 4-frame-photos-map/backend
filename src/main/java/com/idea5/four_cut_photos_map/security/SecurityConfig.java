package com.idea5.four_cut_photos_map.security;

import com.idea5.four_cut_photos_map.security.jwt.filter.JwtAuthorizationFilter;
import com.idea5.four_cut_photos_map.security.jwt.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity  // spring security 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true)  // spring security @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                // TODO: 인가 정책 설정을 @PreAuthorize 로 설정할 것인지, 설정파일에서 할 것인지 고민중
//                .authorizeRequests(
//                        // 소셜로그인 외 모든 요청은 로그인 필수
//                        authorizeRequests -> authorizeRequests
//                                .antMatchers("/member/login/**").permitAll()
//                                .anyRequest()
//                                .authenticated()
//                )
                .cors().disable() // 타 도메인에서 API 호출 가능
                .csrf().disable() // CSRF 토큰 끄기
                .httpBasic().disable() // Spring Security 가 제공하는 기본 로그인 화면 사용X
                .formLogin().disable() // 폼 로그인 방식 사용X
                // jwt 사용을 위해 세션 설정해제
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                )
                // UsernamePasswordAuthenticationFilter 앞단에 jwtAuthorizationFilter 추가
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                // JwtAuthorizationFilter 앞단에 jwtExceptionFilter 추가(Jwt 필터단에서 발생하는 예외처리를 위함)
                .addFilterBefore(
                        jwtExceptionFilter,
                        JwtAuthorizationFilter.class
                );
        return http.build();
    }
}
