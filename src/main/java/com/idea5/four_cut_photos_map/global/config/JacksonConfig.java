package com.idea5.four_cut_photos_map.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @See <a href="https://umanking.github.io/2021/07/24/jackson-localdatetime-serialization/">LocalDateTime 직렬화/역직렬화 문제</a>
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        // ObjectMapper 빈으로 등록할 때 LocalDateTime 직렬화/역직렬화 오류 문제때문에 설정
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
