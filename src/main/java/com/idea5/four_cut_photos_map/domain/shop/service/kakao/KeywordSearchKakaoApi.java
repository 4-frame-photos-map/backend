package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordSearchKakaoApi {

    @Value("${REST_API_KEY}")
    private String kakao_apikey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<KakaoResponseDto> searchByQueryWord(String queryWord, Double longitude, Double latitude, boolean hasRadius) {
        // 1. 결과값 담을 객체 생성
        List<KakaoResponseDto> resultList = new ArrayList<>();

        // 2. header 설정을 위해 HttpHeader 클래스 생성 후 HttpEntity 객체에 넣어준다.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakao_apikey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 3. 파라미터를 사용하여 요청 URL 정의
        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
                + "query=" + queryWord
                + "&x=" + longitude
                + "&y=" + latitude;

        if (hasRadius) apiURL
                += "&sort=distance" // 거리순 정렬
                + "&radius=2000"; // 반경 2km 이내

        // 4. exchange 메서드로 api 호출
        JsonNode documents = restTemplate.exchange(apiURL, HttpMethod.GET, entity, JsonNode.class)
                .getBody()
                .get("documents");

        // 5. JSON -> DTO 역직렬화
        return deserialize(resultList, documents);
    }

    private List<KakaoResponseDto> deserialize(List<KakaoResponseDto> resultList, JsonNode documents) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            for(int i=0; i<documents.size(); i++) {
                JsonNode document = documents.get(i);

                KakaoResponseDto dto = KakaoResponseDto.builder()
                        .placeName(document.get("place_name").textValue())
                        .roadAddressName(document.get("road_address_name").textValue())
                        .longitude(document.get("x").textValue())
                        .latitude(document.get("y").textValue())
                        .distance(Util.distanceFormatting(document.get("distance").textValue()))
                        .build();

                resultList.add(dto);
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }
}