package com.idea5.four_cut_photos_map.security;

import com.idea5.four_cut_photos_map.security.jwt.interceptor.AuthenticationInterceptor;
import com.idea5.four_cut_photos_map.security.jwt.interceptor.OptionalAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @See<a href="https://www.inflearn.com/questions/765181/interceptor-%EC%97%AC%EB%9F%AC%EB%B2%88-%ED%98%B8%EC%B6%9C">인터셉터 preHandle 여러번 호출되는 문제</>
 */
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthenticationInterceptor authenticationInterceptor;
    private final OptionalAuthenticationInterceptor optionalAuthenticationInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3000);
    }

    // 인터셉터 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                // 브라우저에서 http 요청날릴 때 자동으로 /favicon.ico 요청이 실행되어 /error 로 리다이렉트 될 때 인터셉터 preHandle() 실행되는 문제 해결
                .excludePathPatterns("/favicon.ico", "/error")
                .excludePathPatterns("/auth/login/kakao", "/shops/**", "/auth/token");  // 인가작업을 제외할 API 경로 설정
        registry.addInterceptor(optionalAuthenticationInterceptor)
                .addPathPatterns("/shops/**");
    }
}