package com.idea5.four_cut_photos_map.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

import static com.idea5.four_cut_photos_map.global.util.Util.distanceFormatting;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Slf4j
@EnableConfigurationProperties
public class JacksonTest {

    @Value("${REST_API_KEY}")
    private String kakao_apikey;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper mapper = new ObjectMapper();


    @DisplayName("sample test")
    @Test
    void testMethodNameHere() throws JsonProcessingException {
        String keyword = "인생네컷";
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakao_apikey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 3. 파라미터를 사용하여 요청 URL 정의
        String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.JSON?"
                + "query=" + keyword
                + "&x="+127.134898
                + "&y="+36.833922
                + "&sort=distance"; // 거리순


        // 4. exchange 메서드로 api 호출
        String body = restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class).getBody();
        JsonNode root = mapper.readTree(body);
        ArrayList<Object> lists = mapper.treeToValue(root.path("documents"), ArrayList.class);
        for (Object obj : lists) {
            Map<String, String> map = mapper.convertValue(obj, Map.class);

            String distance = distanceFormatting(map.get("distance"));;
            String addressName = map.get("address_name");
            String phone = map.get("phone");
            String placeName = map.get("place_name");
            String roadAddressName = map.get("road_address_name");
            String x = map.get("x");
            String y = map.get("y");

            if (phone.equals(""))
                phone = "미등록";

            KakaoResponseDto dto = KakaoResponseDto.builder()
                    .brand(keyword)
                    .address_name(addressName)
                    .distance(distance)
                    .phone(phone)
                    .placeName(placeName)
                    .roadAddressName(roadAddressName)
                    .x(x)
                    .y(y)
                    .build();

            System.out.println("dto = " + dto);
        }
    }
}
