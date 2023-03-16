package com.idea5.four_cut_photos_map.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.AppConfig;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.global.util.DocumentManagement.Document;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Util {

    private final static ObjectMapper mapper = new ObjectMapper();
    private static ObjectMapper getObjectMapper() {
        return (ObjectMapper) AppConfig.getContext().getBean("objectMapper");
    }

    public static class json {
        // map(json) -> String 변환
        public static Object toStr(Map<String, Object> map) {
            try {
                return getObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        // String -> map(json) 변환
        public static Map<String, Object> toMap(String jsonStr) {
            try {
                return getObjectMapper().readValue(jsonStr, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }

    /**
     * 인자 값들을 map 으로 반환하는 메서드
     * @param args key, value 쌍들
     * @return key, value 들을 Map 으로 변환
     * @param <K> key 타입
     * @param <V> value 타입
     */
    public static<K, V> Map<K, V> mapOf(Object... args) {
        Map<K, V> map = new LinkedHashMap<>();

        int size = args.length / 2;

        for (int i = 0; i < size; i++) {
            int keyIndex = i * 2;
            int valueIndex = keyIndex + 1;

            K key = (K) args[keyIndex];
            V value = (V) args[valueIndex];

            map.put(key, value);
        }
        return map;
    }


    public static String distanceFormatting(String distance){
        int length = distance.length();
        if(distance.equals("")) // 공백일 시, 알 수없음으로 반환
            return "unknown";
        if(length <= 3) // distance -> m
            return distance + "m";
        double dkm = Integer.parseInt(distance) / 1000.0; // km 환산한 값
        if(length == 4) { // distance -> km
            if(dkm % 1 == 0)
                return String.format("%.0fkm", dkm);
            // 소수점 둘째 자리에서 반올림
            return String.format("%.1fkm", dkm);
        }
        // 소수점 첫째 자리에서 반올림
        return String.format("%.0fkm", dkm);
    }



    public static List<KakaoResponseDto> documentToObject(DocumentManagement body, String searchBrand){
        List<KakaoResponseDto> dtos = new ArrayList<>();

        for (int i = 0; i < body.getDocuments().length; i++) {
            String brand = searchBrand;
            String addressName = body.getDocuments()[i].getAddress_name();
            String distance = body.getDocuments()[i].getDistance();
            distance = distanceFormatting(distance);
            String placeName = body.getDocuments()[i].getPlace_name();
            String roadAddressName = body.getDocuments()[i].getRoad_address_name();
            String longitude = body.getDocuments()[i].getX();
            String latitude = body.getDocuments()[i].getY();
            String phone = body.getDocuments()[i].getPhone();

            if (phone.equals(""))
                phone = "미등록";

            KakaoResponseDto dto = KakaoResponseDto.builder()
                    .brand(brand)
                    .address_name(addressName)
                    .distance(distance)
                    .phone(phone)
                    .placeName(placeName)
                    .roadAddressName(roadAddressName)
                    .x(longitude)
                    .y(latitude)
                    .build();

            dtos.add(dto);
        }
        return dtos;
    }

    public static List<KakaoResponseDto> jackson2(String body, String brandName){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<KakaoResponseDto> list = new ArrayList<>();
        try{

            DocumentManagement documentManagement = mapper.readValue(body, DocumentManagement.class);
            Document[] documents = documentManagement.getDocuments();
            for (Document document : documents) {
                String phone = document.getPhone();
                if (phone.equals("")) {
                    document.setPhone("미등록");
                }
                document.setDistance(distanceFormatting(document.getDistance()));

                KakaoResponseDto dto = KakaoResponseDto.from(document, brandName);
                list.add(dto);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return list;

    }

    public static List<KakaoResponseDto> jackson(String body, String brandName) {

        ArrayList<KakaoResponseDto> list = new ArrayList<>();
        try{
            JsonNode root = mapper.readTree(body);
            ArrayList<Object> lists = mapper.treeToValue(root.path("documents"), ArrayList.class);
            for (Object obj : lists) {
                Map<String, String> map = mapper.convertValue(obj, Map.class);

                String distance = distanceFormatting(map.get("distance"));
                String addressName = map.get("address_name");
                String phone = map.get("phone");
                String placeName = map.get("place_name");
                String roadAddressName = map.get("road_address_name");
                String x = map.get("x");
                String y = map.get("y");

                if (phone.equals(""))
                    phone = "미등록";

                KakaoResponseDto dto = KakaoResponseDto.builder()
                        .brand(brandName)
                        .address_name(addressName)
                        .distance(distance)
                        .phone(phone)
                        .placeName(placeName)
                        .roadAddressName(roadAddressName)
                        .x(x)
                        .y(y)
                        .build();

                list.add(dto);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
        return list;
    }

    // 난수 생성
    public static String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append(random.nextInt(9));
        }
        return sb.toString();
    }
}
