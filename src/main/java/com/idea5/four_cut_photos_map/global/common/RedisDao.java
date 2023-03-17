package com.idea5.four_cut_photos_map.global.common;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * RedisTemplate 를 쉽게 사용하기 위해 만든 DAO
 * - RedisTemplate: Redis Command 를 도와주는 Template
 * @See <a href="https://sol-devlog.tistory.com/22">RedisDao 참고</a>
 * @See <a href="https://zkdlu.github.io/2020-12-29/redis02-spring-boot%EC%97%90%EC%84%9C-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0/">redisTemplate</a>
 */
@Component
public class RedisDao {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisDao(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // (key, value) 쌍 저장(만료기간 설정X)
    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    // (key, value) 쌍 저장(만료기간 설정O)
    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    // key 로 value 조회
    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    // key 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // key 존재여부 확인
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 회원의 kakao access token 을 저장하는 key
    public static String getKakaoAtkKey(Long memberId) {
        return "member:" + memberId + ":kakao_access_token";
    }

    // 회원의 jwt refresh token 을 저장하는 key
    public static String getRtkKey(Long memberId) {
        return "member:" + memberId + ":jwt_refresh_token";
    }

    // 블랙리스트 jwt access token 을 관리하는 key
    public static String getBlackListAtkKey(String accessToken) {
        return "jwt_black_list:" + accessToken;
    }
}
