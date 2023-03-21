package com.idea5.four_cut_photos_map.domain.shop.service.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
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
public class KeywordSearchKakaoApi {

    @Value("${REST_API_KEY}")
    private String kakao_apikey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<KakaoKeywordResponseDto> searchByQueryWord(String queryWord, Double longitude, Double latitude, boolean hasRadius) {
        // 1. 결과값 담을 객체 생성
        List<KakaoKeywordResponseDto> resultList = new ArrayList<>();

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
        String body = restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class).getBody();

        // 5. JSON -> DTO 역직렬화
        return deserialize(resultList, body);
    }

    private List<KakaoKeywordResponseDto> deserialize(List<KakaoKeywordResponseDto> resultList, String body) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            JsonNode node = objectMapper.readTree(body);
            List<String> countList = node.get("documents").findValuesAsText("place_name");

            for(int i=0; i<countList.size(); i++) {
                JsonNode documents = node.get("documents").get(i);

                KakaoKeywordResponseDto dto = KakaoKeywordResponseDto.builder()
                        .placeName(documents.get("place_name").textValue())
                        .roadAddressName(documents.get("road_address_name").textValue())
                        .longitude(documents.get("x").textValue())
                        .latitude(documents.get("y").textValue())
                        .distance(Util.distanceFormatting(documents.get("distance").textValue()))
                        .phone(documents.get("phone").textValue().equals("") ?
                                "미등록" : documents.get("phone").textValue()) // todo: 필요성 논의 후 필요없다면 제거 필요
                        .build();

                resultList.add(dto);
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }

    // 브랜드별 검색(페이징)
//    public List<KakaoResponseDto> searchByBrand(RequestBrandSearch request, int page){
//        // 2. header 설정을 위해 HttpHeader 클래스 생성 후 HttpEntity 객체에 넣어준다.
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + kakao_apikey);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // 3. 파라미터를 사용하여 요청 URL 정의
//        // ex) https://dapi.kakao.com/v2/local/search/keyword?query=${}&x=${}&y=${}&sort=distance
//        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
//                + "query=" + request.getBrand()
//                + "&x="+request.getLongitude()
//                + "&y="+request.getLatitude()
//                + "&size=15"
//                + "&page="+page
//                + "&sort=distance" // 거리순
//                + "&radius=2000"; // 반경 2km 이내
//
//        System.out.println("apiURL = " + apiURL);
//
//        // 4. exchange 메서드로 api 호출
//        String body = restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class).getBody();
//        List<KakaoResponseDto> list = jackson2(body, request.getBrand());
//        return list;
//    }

    // 브랜드별 Map Marker
//    public List<KakaoResponseDto> searchMarkers(RequestShop shop, String brandName) {
//        // 2. header 설정을 위해 HttpHeader 클래스 생성 후 HttpEntity 객체에 넣어준다.
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + kakao_apikey);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // 3. 파라미터를 사용하여 요청 URL 정의
//        // ex) https://dapi.kakao.com/v2/local/search/keyword?query=${}&x=${}&y=${}&sort=distance
//        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
//                + "query=" + brandName
//                + "&x="+shop.getLongitude()
//                + "&y="+shop.getLatitude()
//                + "&sort=distance" // 거리순
//                + "&radius=2000"; // 반경 2km이내
//        System.out.println("apiURL = " + apiURL);
//
//        String body = restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class).getBody();
//        List<KakaoResponseDto> kakaoResponseDtos = jackson2(body, brandName);
//        return kakaoResponseDtos;
//    }

    // 브랜드검색과 현재위치기준 검색에 사용했던 기존 역직렬화 메서드
//    public List<KakaoResponseDto> jackson2(String body, String brandName){
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        ArrayList<KakaoResponseDto> list = new ArrayList<>();
//        try{
//
//            DocumentManagement documentManagement = objectMapper.readValue(body, DocumentManagement.class);
//            DocumentManagement.Document[] documents = documentManagement.getDocuments();
//            for (DocumentManagement.Document document : documents) {
//                String phone = document.getPhone();
//                if (phone.equals("")) {
//                    document.setPhone("미등록");
//                }
//                document.setDistance(Util.distanceFormatting(document.getDistance()));
//
//                KakaoResponseDto dto = KakaoResponseDto.from(document, brandName);
//                list.add(dto);
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//        return list;
//
//    }
}