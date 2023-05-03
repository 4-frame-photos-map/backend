package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public static final String DEFAULT_QUERY_WORD = "즉석사진";
    public static final String CATEGORY_NAME = "사진";



    public List<KakaoMapSearchDto> searchByQueryWord(String queryWord, Double userLat, Double userLng) {
        List<KakaoMapSearchDto> resultList = new ArrayList<>();

        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", queryWord + DEFAULT_QUERY_WORD)
                .queryParam("y", userLat)
                .queryParam("x", userLng);

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

    public List<KakaoMapSearchDto> searchByQueryWord(String queryWord, Integer radius, Double userLat, Double userLng, Double mapLat, Double mapLng) {
        List<KakaoMapSearchDto> resultList = new ArrayList<>();

        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", queryWord + DEFAULT_QUERY_WORD)
                .queryParam("y", mapLat)
                .queryParam("x", mapLng)
                .queryParam("sort", "distance")
                .queryParam("radius", radius);

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents;
        try {
            documents = getDocuments(apiUrl);
        } catch (Exception e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        // 3. JSON -> DTO 역직렬화
        return deserialize(resultList, documents, userLat, userLng, mapLat, mapLng);
    }

    public String[] searchSingleShopByQueryWord(Shop dbShop, Double userLat, Double userLng) {
        // 1. Redis에서 조회
        String[] cachedArr = getShopInfoFromCacheAndCalculateDist(dbShop, userLat, userLng);
        if (cachedArr != null) {return cachedArr;}

        String[] queryWords = {dbShop.getPlaceName(), dbShop.getAddress()};
        for (String queryWord : queryWords) {
            // 2. API 호출을 위한 요청 설정
            String apiPath = "/v2/local/search/keyword.json";
            String apiUrl = UriComponentsBuilder.fromPath(apiPath)
                    .queryParam("query", queryWord + DEFAULT_QUERY_WORD)
                    .queryParam("y", userLat)
                    .queryParam("x", userLng)
                    .queryParam("")
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
            // 따라서, 여러 데이터 중 요청 도로명 주소와 브랜드명으로 비교하여 일치하는 데이터 1개만 찾아서 반환
            String[] results = matchAndDeserialize(documents, dbShop.getAddress(), dbShop.getPlaceName());
            if(results != null) {return results;}
        }
        return null;
    }

    public String convertCoordinateToAddress(Double mapLat, Double mapLng) {
        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/geo/coord2address.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("y", mapLat)
                .queryParam("x", mapLng);

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents;
        try {
            documents = getDocuments(apiUrl);
        } catch (Exception e) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        // 3. JSON -> DTO 역직렬화
        // 도로명 주소가 없다면 지번 주소 반환
        String address = Optional.ofNullable(documents.get(0).get("road_address"))
                .map(jsonNode -> jsonNode.get("address_name"))
                .map(JsonNode::asText)
                .orElse(documents.get(0).get("address").get("address_name").asText());

        return address;
    }

    private List<KakaoMapSearchDto> deserialize(List<KakaoMapSearchDto> resultList, JsonNode documents) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            if(document.get("category_name").asText().contains(CATEGORY_NAME)) {
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

    private List<KakaoMapSearchDto> deserialize(List<KakaoMapSearchDto> resultList, JsonNode documents, Double userLat, Double userLng,
                                                                                                        Double mapLat, Double mapLng) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        boolean isUserAndMapCoordSame = (userLat == mapLat) && (userLng == mapLng);
        for (JsonNode document : documents) {
            if (document.get("category_name").asText().contains(CATEGORY_NAME)) {
                try {
                    KakaoMapSearchDto dto = objectMapper.treeToValue(document, KakaoMapSearchDto.class);

                    if(!isUserAndMapCoordSame) {
                        // 사용자 중심좌표를 기준으로 지점으로부터의 거리 갱신
                        Double placeLat = Double.parseDouble(dto.getLatitude());
                        Double placeLng = Double.parseDouble(dto.getLongitude());
                        dto.setDistance(Util.calculateDist(placeLat, placeLng, userLat, userLng));
                    }

                    resultList.add(dto);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return resultList;
    }

    private String[] matchAndDeserialize(JsonNode documents, String dbAddress, String dbPlaceName) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            try {
                KakaoMapSearchDto dto = objectMapper.treeToValue(document, KakaoMapSearchDto.class);

                String apiPlaceName = dto.getPlaceName();
                String apiRoadAddressName = dto.getRoadAddressName();
                String apiAddressName = dto.getAddressName();

                if (isMatchedShop(dbPlaceName, apiPlaceName, dbAddress, apiRoadAddressName) ||
                        isMatchedShop(dbPlaceName, apiPlaceName, dbAddress, apiAddressName)) {
                    return new String[]{
                            dto.getPlaceUrl(),
                            dto.getLatitude(),
                            dto.getLongitude(),
                            Util.distanceFormatting(dto.getDistance())
                    };
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Redis에서 지점 정보 가져와서 지점으로부터 현재 위치까지의 거리 계산하기
     * @param dbShop
     * @param userLat
     * @param userLng
     * @return placeUrl, placeLat, placeLng, distance
     */
    private String[] getShopInfoFromCacheAndCalculateDist(Shop dbShop, Double userLat, Double userLng) {
        String cacheKey = redisDao.getShopInfoKey(dbShop.getId());
        String cachedData = redisDao.getValues(cacheKey);

        if (cachedData != null) {
            String[] cachedArr = cachedData.split(",");
            log.info("=======Shop Info Cache Hit=======");
            String distance = Util.calculateDist(
                    Double.parseDouble(cachedArr[1]), // placeLat
                    Double.parseDouble(cachedArr[2]), // placeLng
                    userLat,
                    userLng
            );
            return new String[]{cachedArr[0], cachedArr[1], cachedArr[2], distance};
        }
        log.info("=======Shop Info Cache Miss=======");
        return null;
    }

    public boolean isMatchedShop(String dbPlaceName, String apiPlaceName, String dbAddress, String apiAddress) {
        return dbPlaceName.equals(apiPlaceName) || dbAddress.contains(apiAddress);
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