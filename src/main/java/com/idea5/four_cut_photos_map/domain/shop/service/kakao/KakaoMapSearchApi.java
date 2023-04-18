package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.TOO_MANY_REQUESTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapSearchApi {
    private final WebClient firstWebClient;
    private final WebClient secondWebClient;
    private final WebClient thirdWebClient;
    private final WebClient fourthWebClient;
    private final RedisDao redisDao;
    private final ObjectMapper objectMapper;
    public final int radius = 2000;
    public static final String DEFAULT_QUERY_WORD = "즉석사진";


    public List<KakaoMapSearchDto> searchByQueryWord(String queryWord, Double latitude, Double longitude, boolean hasRadius) {
        List<KakaoMapSearchDto> resultList = new ArrayList<>();

        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", queryWord + DEFAULT_QUERY_WORD)
                .queryParam("x", longitude)
                .queryParam("y", latitude);

        if (hasRadius) {
            uriBuilder.queryParam("sort", "distance")
                    .queryParam("radius", radius);
        }

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents;
        try {
            documents = getDocuments(apiUrl);
        } catch (Exception e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        // 3. JSON -> DTO 역직렬화
        return deserialize(resultList, documents);
    }

    public String[] searchByRoadAddressName(String roadAddressName, String placeName) {
        // 1-1. Redis에서 조회
        String cacheKey = redisDao.getRoadAddressKey(roadAddressName);
        String cachedData = redisDao.getValues(cacheKey);

        if (cachedData != null) {
            String[] cached = cachedData.split((","));
            // 1-2. apiShop의 brand명이 dbShop의 placeName에 포함되는지 확인
            if(placeName.contains(cached[0].split(" ")[0])) {
                log.info("=====RoadAddressName Cache Hit=====");
                return cachedData.split(",");
            }
        }
        log.info("=====RoadAddressName Cache Miss=====");

        // 2. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        String apiUrl = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", roadAddressName + DEFAULT_QUERY_WORD)
                .build()
                .toString();

        // 3. API 호출
        JsonNode documents;
        try {
            documents = getDocuments(apiUrl);
        } catch (Exception e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        // 4. JSON -> String 역직렬화
        // 도로명주소와 DEFAULT_QUERY_WORD로 검색 시
        // 100% 일치하는 데이터가 항상 상단에 노출되지 않음
        // 따라서, 여러 데이터 중 요청 도로명 주소와 일치하는 데이터 1개만 찾아서 반환
        String[] result = matchAndDeserialize(documents, roadAddressName, placeName);
        if(result != null){
            // 5. Redis에 데이터 저장
            redisDao.setValues(cacheKey, String.join(",", result), Duration.ofDays(1));
            return result;
        } else {
            return null;
        }
    }

    public String[] searchByRoadAddressName(String roadAddressName, String placeName, Double curLnt, Double curLat) {
        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        String apiUrl = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", roadAddressName + DEFAULT_QUERY_WORD)
                .queryParam("x",curLnt)
                .queryParam("y",curLat)
                .build()
                .toString();

        // 2. API 호출
        JsonNode documents;
        try {
            documents = getDocuments(apiUrl);
        } catch (Exception e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        // 3. JSON -> String 역직렬화
        // 도로명주소와 DEFAULT_QUERY_WORD로 검색 시
        // 100% 일치하는 데이터가 항상 상단에 노출되지 않음
        // 따라서, 여러 데이터 중 요청 도로명 주소와 일치하는 데이터 1개만 찾아서 반환
        String[] result = matchAndDeserializeWithCurLocation(documents, roadAddressName, placeName);
        return result;
    }

    private List<KakaoMapSearchDto> deserialize(List<KakaoMapSearchDto> resultList, JsonNode documents) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            if(document.get("category_name").asText().contains(DEFAULT_QUERY_WORD)) {
                try {
                    KakaoMapSearchDto dto = objectMapper.treeToValue(document, KakaoMapSearchDto.class);
                    dto.setDistance(Util.distanceFormatting(dto.getDistance()));
                    resultList.add(dto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return resultList;
    }

    private String[] matchAndDeserialize(JsonNode documents, String roadAddressName, String placeName) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            if (document.get("road_address_name").asText().equals(roadAddressName)) {
                if(placeName.contains(document.get("place_name").asText().split(" ")[0])) {
                    return new String[]{
                            document.get("place_name").asText(), document.get("place_url").asText(),
                            document.get("x").asText(), document.get("y").asText()
                    };
                }
            }
        }
        return null;
    }

    private String[] matchAndDeserializeWithCurLocation(JsonNode documents, String roadAddressName, String placeName) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            if (document.get("road_address_name").asText().equals(roadAddressName)) {
                if(placeName.contains(document.get("place_name").asText().split(" ")[0])) {
                    return new String[]{
                            document.get("place_name").asText(), Util.distanceFormatting(document.get("distance").asText())
                    };
                }
            }
        }
        return null;
    }
    private JsonNode getDocuments(String apiUrl) {
        WebClient webClient = firstWebClient;

        try {
            return webClient.get()
                    .uri(apiUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block()
                    .get("documents");
        } catch (WebClientResponseException e) {
            webClient = secondWebClient;
        }

        try {
            return webClient.get()
                    .uri(apiUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block()
                    .get("documents");
        } catch (WebClientResponseException e) {
            webClient = thirdWebClient;
        }

        try {
            return webClient.get()
                    .uri(apiUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block()
                    .get("documents");
        } catch (WebClientResponseException e) {
            webClient = fourthWebClient;
        }

        try {
            return webClient.get()
                    .uri(apiUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block()
                    .get("documents");
        } catch (WebClientResponseException e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }
    }
}