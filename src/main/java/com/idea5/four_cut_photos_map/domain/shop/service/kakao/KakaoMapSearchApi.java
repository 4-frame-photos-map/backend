package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapSearchApi {

    @Value("${REST_API_KEY}")
    private String kakao_apikey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    public final int radius= 2000;
    public static final String DEFAULT_QUERY_WORD = "즉석사진";




    public List<KakaoMapSearchDto> searchByQueryWord(String queryWord, Double longitude, Double latitude, boolean hasRadius) {
        List<KakaoMapSearchDto> resultList = new ArrayList<>();

        // 1. header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakao_apikey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. 요청 URL 정의
        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
                + "query=" + queryWord + DEFAULT_QUERY_WORD
                + "&x=" + longitude
                + "&y=" + latitude;

        if (hasRadius) apiURL
                += "&sort=distance" // 거리순 정렬
                + "&radius=" + radius; // 반경 2km 이내

        // 3. api 호출
        JsonNode documents = restTemplate.exchange(apiURL, HttpMethod.GET, entity, JsonNode.class)
                .getBody()
                .get("documents");

        // 4. JSON -> DTO 역직렬화
        return deserialize(resultList, documents);
    }

    private List<KakaoMapSearchDto> deserialize(List<KakaoMapSearchDto> resultList, JsonNode documents) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            try {
                KakaoMapSearchDto dto = objectMapper.treeToValue(document, KakaoMapSearchDto.class);
                dto.setDistance(Util.distanceFormatting(dto.getDistance()));
                resultList.add(dto);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return resultList;
    }
}