package com.idea5.four_cut_photos_map.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${map.kakao.first-key}")
    private String FIRST_API_KEY;
    @Value("${map.kakao.second-key}")
    private String SECOND_API_KEY;
    @Value("${map.kakao.third-key}")
    private String THIRD_API_KEY;
    @Value("${map.kakao.fourth-key}")
    private String FOURTH_API_KEY;

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
    public WebClient thirdWebClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + THIRD_API_KEY)
                .build();
    }

    @Bean
    public WebClient fourthWebClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + FOURTH_API_KEY)
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}