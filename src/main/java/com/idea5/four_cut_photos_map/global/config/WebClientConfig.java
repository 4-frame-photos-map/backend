package com.idea5.four_cut_photos_map.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${REST_API_KEY}")
    public String FIRST_API_KEY;
    @Value("${oauth2.kakao.client-id}")
    public String SECOND_API_KEY;

    @Bean
    public WebClient firstWebClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + FIRST_API_KEY)
                .build();
    }
    @Bean
    public WebClient secondWebClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + SECOND_API_KEY)
                .build();
    }
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}