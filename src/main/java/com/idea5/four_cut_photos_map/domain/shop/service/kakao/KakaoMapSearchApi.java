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
                + "&radius=2000"; // 반경 2km 이내

        // 3. api 호출
        JsonNode documents = restTemplate.exchange(apiURL, HttpMethod.GET, entity, JsonNode.class)
                .getBody()
                .get("documents");

        // 4. JSON -> DTO 역직렬화
        return deserialize(resultList, documents);
    }

    public String[] searchByRoadAddressName(String roadAddressName) {
        // 1. header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakao_apikey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. 요청 URL 정의
        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
                + "query=" + roadAddressName + DEFAULT_QUERY_WORD
                + "&size=1"; // 정확도순 상위 하나의 지점만 응답받도록 제한

        // 3. api 호출
        JsonNode document = restTemplate.exchange(apiURL, HttpMethod.GET, entity, JsonNode.class)
                .getBody()
                .get("documents")
                .get(0);

        // 4. JSON -> String 역직렬화
        // 100% 일치 결과 없으면 유사도 제일 높은 장소 받아오기 때문에
        // 요청 도로명 주소와 완전히 일치하는지 검사 필요
        if(document.get("road_address_name").asText().equals(roadAddressName))
            return new String[] {document.get("place_name").asText(), document.get("place_url").asText()};
        else return null;
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