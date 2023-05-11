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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

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


    /**
     * Kakao Maps API 호출하여 사용자가 입력한 검색 키워드로 즉석사진 지점 정보 가져오는 메서드입니다.
     * 지도 중심좌표가 아닌 사용자 현재위치 좌표로 검색하며, 검색 반경에 제한이 없습니다.
     * @param queryWord 검색할 키워드
     * @param userLat
     * @param userLng
     * @return List<KakaoMapSearchDto>
     */
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
        JsonNode documents = getDocuments(apiUrl);

        // 3. JSON -> DTO 역직렬화
        return deserialize(resultList, documents);
    }

    /**
     * Kakao Maps API 호출하여 사용자가 선택한 브랜드의 즉석사진 지점 정보 가져오는 메서드입니다.
     * 사용자 현재위치 좌표가 아닌 지도 중심좌표로 검색하며, 검색 반경에 제한 있습니다.
     * @param queryWord 사용자가 선택한 브랜드
     * @param radius
     * @param userLat
     * @param userLng
     * @param mapLat
     * @param mapLng
     * @return List<KakaoMapSearchDto>
     */
    public List<KakaoMapSearchDto> searchByQueryWord(String queryWord, Integer radius, Double userLat, Double userLng, Double mapLat, Double mapLng) {
        List<KakaoMapSearchDto> resultList = new ArrayList<>();

        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/keyword.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", queryWord + DEFAULT_QUERY_WORD)
                .queryParam("y", mapLat)
                .queryParam("x", mapLng)
                .queryParam("radius", radius);

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents = getDocuments(apiUrl);

        // 3. JSON -> DTO 역직렬화
        return deserialize(resultList, documents, userLat, userLng, mapLat, mapLng);
    }

    /**
     * Kakao Maps API 호출하여 지점명이나 주소로 특정 즉석사진 지점 정보 가져오는 메서드입니다.
     * @param dbShop
     * @param userLat
     * @param userLng
     * @return 검색 결과로부터 가져온 특정 지점의 카카오맵 바로가기 URL(placeUrl), 위도(placeLat), 경도(placeLng),
     *          사용자의 현재위치 좌표로부터 지점까지의 거리(distance)를 반환합니다.
     */
    public String[] searchSingleShopByQueryWord(Shop dbShop, Double userLat, Double userLng, String...queryWords) {
        // 1. Redis에서 조회
        String[] cachedArr = getShopInfoFromCacheAndCalcDist(dbShop, userLat, userLng);
        if (cachedArr != null) {return cachedArr;}

        for (String queryWord : queryWords) {
            // 2. API 호출을 위한 요청 설정
            String apiPath = "/v2/local/search/keyword.json";
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(apiPath)
                    .queryParam("query", queryWord);

            if (userLat != null && userLng != null) {
                builder.queryParam("y", userLat)
                        .queryParam("x", userLng);
            }

            String apiUrl = builder.build().toString();

            // 3. API 호출
            JsonNode documents = getDocuments(apiUrl);

            // 4. JSON -> String 역직렬화
            // 도로명주소와 DEFAULT_QUERY_WORD로 검색 시
            // 100% 일치하는 데이터가 항상 상단에 노출되지 않음
            // 따라서, 여러 데이터 중 요청 도로명 주소와 브랜드명으로 비교하여 일치하는 데이터 1개만 찾아서 반환
            String[] results = matchAndDeserialize(documents, dbShop.getAddress(), dbShop.getPlaceName());
            if(results != null) {return results;}
        }
        return null;
    }

    /**
     * Kakao Maps API 호출하여 지도 중심 좌표를 주소로 변환하는 메서드입니다.
     * @param mapLat
     * @param mapLng
     * @return address
     */
    public String convertCoordinateToAddress(Double mapLat, Double mapLng) {
        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/geo/coord2address.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("y", mapLat)
                .queryParam("x", mapLng);

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents = getDocuments(apiUrl);

        // 3. JSON -> DTO 역직렬화
        // 도로명 주소가 없다면 지번 주소 반환
        String address = Optional.ofNullable(documents.get(0).get("road_address"))
                .map(jsonNode -> jsonNode.get("address_name"))
                .map(JsonNode::asText)
                .orElse(documents.get(0).get("address").get("address_name").asText());

        return address;
    }

    /**
     * Kakao Maps API 호출하여 지점 주소를 좌표로 변환하고, 사용자의 현재위치로부터 지점까지의 거리를 계산하는 메서드입니다.
     * @param dbShop
     * @param userLat
     * @param userLng
     * @return distance
     */
    public String convertAddressToCoordAndCalcDist(Shop dbShop, Double userLat, Double userLng) {
        // 1. API 호출을 위한 요청 설정
        String apiPath = "/v2/local/search/address.json";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath)
                .queryParam("query", dbShop.getAddress())
                .queryParam("size", 1);

        String apiUrl = uriBuilder.build().toString();

        // 2. API 호출
        JsonNode documents = getDocuments(apiUrl);

        // 3. JSON -> DTO 역직렬화 및 사용자 현재위치 좌표로부터 지점까지의 거리 계산
        if(documents.get(0).hasNonNull("y") && documents.get(0).hasNonNull("x")) {
            return Util.calculateDist(
                    documents.get(0).get("y").asDouble(),documents.get(0).get("x").asDouble(),
                    userLat, userLng
            );
        } else {
            return null;
        }
    }

    private List<KakaoMapSearchDto> deserialize(List<KakaoMapSearchDto> resultList, JsonNode documents) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (JsonNode document : documents) {
            if(isCategoryNameMatched(document)) {
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
        for (JsonNode document : documents) {
            if (isCategoryNameMatched(document)) {
                try {
                    KakaoMapSearchDto dto = objectMapper.treeToValue(document, KakaoMapSearchDto.class);

                    if((userLat != mapLat) || (userLng != mapLng)) {
                        // 사용자 현재위치 좌표로부터 지점까지의 거리 갱신
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
        resultList.sort((dto1, dto2) -> {
            double dist1 = Double.parseDouble(dto1.getDistance().replaceAll("[^\\d.]", ""));
            double dist2 = Double.parseDouble(dto2.getDistance().replaceAll("[^\\d.]", ""));
            String unit1 = dto1.getDistance().replaceAll("[\\d.]", "");
            String unit2 = dto2.getDistance().replaceAll("[\\d.]", "");

            if (unit1.equals("m")) {dist1 /= 1000;}
            if (unit2.equals("m")) {dist2 /= 1000;}

            return Double.compare(dist1, dist2);
        });
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

                if (isCategoryNameMatched(document)
                        && (isMatchedShop(dbPlaceName, apiPlaceName, dbAddress, apiRoadAddressName)
                            || isMatchedShop(dbPlaceName, apiPlaceName, dbAddress, apiAddressName))) {
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
     * Redis에서 지점 정보 가져오는 메서드입니다.
     * @param dbShop
     * @return placeUrl, placeLat, placeLng
     */
    public String[] getShopInfoFromCache(Shop dbShop) {
        String cacheKey = redisDao.getShopInfoKey(dbShop.getId());
        String cachedData = redisDao.getValues(cacheKey);

        if (cachedData != null) {
            log.info("=======Shop Info Cache Hit=======");
            return cachedData.split(",");
        }
        log.info("=======Shop Info Cache Miss=======");
        return null;
    }

    /**
     * Redis에서 지점 정보 가져와서 지점으로부터 현재 위치까지의 거리 계산하는 메서드입니다.
     * @param dbShop
     * @param userLat
     * @param userLng
     * @return placeUrl, placeLat, placeLng, distance
     */
    public String[] getShopInfoFromCacheAndCalcDist(Shop dbShop, Double userLat, Double userLng) {
        String[] cachedArr = getShopInfoFromCache(dbShop);

        if (cachedArr != null) {
            if(userLat != null && userLng != null) {
                String distance = Util.calculateDist(
                        Double.parseDouble(cachedArr[1]), // placeLat
                        Double.parseDouble(cachedArr[2]), // placeLng
                        userLat,
                        userLng
                );
                return new String[]{cachedArr[0], cachedArr[1], cachedArr[2], distance};
            } else {
                return new String[]{cachedArr[0], cachedArr[1], cachedArr[2], ""};
            }
        }
        return null;
    }

    private boolean isCategoryNameMatched(JsonNode document) {
        return document.get("category_name").asText().contains(CATEGORY_NAME);
    }

    private boolean isMatchedShop(String dbPlaceName, String apiPlaceName, String dbAddress, String apiAddress) {
        String apiBrandName = apiPlaceName.split(" ")[0];
        dbPlaceName = Util.removeSpace(dbPlaceName);
        apiPlaceName = Util.removeSpace(apiPlaceName);
        dbAddress = Util.removeSpace(dbAddress);
        apiAddress = Util.removeSpace(apiAddress);

        return dbPlaceName.contains(apiPlaceName)
                || dbAddress.contains(apiAddress)
                || (dbPlaceName.contains(apiBrandName) && (apiAddress.contains(dbAddress) || dbAddress.contains(apiAddress)));
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
            if (e.getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                webClient = secondWebClient;

                try {
                    return webClient.get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block()
                            .get("documents");
                } catch (WebClientResponseException ex) {
                    webClient = thirdWebClient;

                    try {
                        return webClient.get()
                                .uri(apiUrl)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(JsonNode.class)
                                .block()
                                .get("documents");
                    } catch (WebClientResponseException exc) {
                        webClient = fourthWebClient;

                        try {
                            return webClient.get()
                                    .uri(apiUrl)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .retrieve()
                                    .bodyToMono(JsonNode.class)
                                    .block()
                                    .get("documents");
                        } catch (WebClientResponseException exce) {
                            if (exce.getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                                throw new BusinessException(TOO_MANY_REQUESTS);
                            } else {
                                log.error(exce.getMessage());
                            }
                        }
                    }
                }
            } else {
                log.error(e.getMessage());
            }
        }

        return null;
    }
}