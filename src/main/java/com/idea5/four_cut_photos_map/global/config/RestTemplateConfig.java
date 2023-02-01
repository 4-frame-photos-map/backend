package com.idea5.four_cut_photos_map.global.config;

import com.idea5.four_cut_photos_map.global.error.RestTemplateResponseErrorHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 다른 서버에 Rest 요청을 할 때 필요한 설정을 하는 클래스
 */
@Configuration
public class RestTemplateConfig {
    private final int READ_TIMEOUT = 5000;      // 읽기시간초과(ms)
    private final int CONNECT_TIMEOUT = 5000;   // 연결시간초과(ms)
    private final int MAX_CONN_TOTAL = 100;     // 최대 오픈되는 커넥션 수
    private final int MAX_CONN_PER_ROUTE = 5;   // IP, 포트 1쌍에 수행할 커넥션 수

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectTimeout(CONNECT_TIMEOUT);

        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(MAX_CONN_TOTAL)
                .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                .build();

        factory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        // 에러 핸들러 설정 추가
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }
}