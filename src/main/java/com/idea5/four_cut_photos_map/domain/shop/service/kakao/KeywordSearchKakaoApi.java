package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.KaKaoSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class KeywordSearchKakaoApi {

    @Value("${REST_API_KEY}")
    private String kakao_apikey;
    private final RestTemplate restTemplate;

    public KaKaoSearchResponseDto searchByKeyword(String keyword) {
        // 1. 결과값 담을 객체 생성
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        // 2. header 설정을 위해 HttpHeader 클래스 생성 후 HttpEntity 객체에 넣어준다.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakao_apikey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 3. 파라미터를 사용하여 요청 URL 정의
        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
                + "query=" + keyword; // request param (x, y, radius 등 검색 조건 추가 가능)

        // 4. exchange 메서드로 api 호출
        return restTemplate.exchange(apiURL, HttpMethod.GET, entity,KaKaoSearchResponseDto.class).getBody();
    }
}