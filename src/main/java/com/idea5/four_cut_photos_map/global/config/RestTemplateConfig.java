package com.idea5.four_cut_photos_map.global.config;


import com.idea5.four_cut_photos_map.security.handler.RestTemplateLoggingRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 다른 서버에 Rest 요청을 할 때 필요한 설정을 하는 클래스
 */
@Configuration
@Slf4j
public class RestTemplateConfig {
    private final int READ_TIMEOUT = 5000;      // 읽기시간초과(ms)
    private final int CONNECT_TIMEOUT = 5000;   // 연결시간초과(ms)
    private final int MAX_CONN_TOTAL = 100;     // 최대 오픈되는 커넥션 수
    private final int MAX_CONN_PER_ROUTE = 5;   // IP, 포트 1쌍에 수행할 커넥션 수
    private RestTemplateLoggingRequestInterceptor restTemplateLoggingRequestInterceptor;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {

        // Apache HttpComponents
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(MAX_CONN_TOTAL)
                .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectTimeout(CONNECT_TIMEOUT);

        RestTemplate restTemplate = restTemplateBuilder
                .setReadTimeout(Duration.ofMillis(READ_TIMEOUT))
                .setConnectTimeout(Duration.ofMillis(CONNECT_TIMEOUT))
                .additionalMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8)) //로깅을 위한 메시지 컨버터 추가
                .additionalInterceptors(restTemplateLoggingRequestInterceptor) // 로깅을 위한 인터셉터 추가
                .build();

        // 로깅 DEBUG 레벨이 활성화된 경우에만 BufferingClientHttpRequest 사용
        // BufferingClientHttpRequestFactory를 통해 Stream 콘텐츠를 메모리에 버퍼링 함으로써 Body 값을 두 번 읽을 수 있음
        //  ResponseEntity의 Body는 한번 사용되면 소멸되는 Stream 이기 때문에 로깅 목적으로 1번, 비즈니스 로직 목적으로 1번 총 2번 읽어옴
        if (log.isDebugEnabled()) {
            ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
            restTemplate.setRequestFactory(clientHttpRequestFactory);
            return restTemplate;
        }
        return restTemplate;
    }
}
