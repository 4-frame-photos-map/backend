package com.idea5.four_cut_photos_map.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.AppConfig;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {
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
            return distance+"m";
        else if(length == 4) { // distance -> km
            int num1 = distance.charAt(0) -'0'; // 첫째자리
            int num2 = distance.charAt(1) -'0'; // 둘째자리
            int num3 = distance.charAt(2) -'0'; // 셋째자리
            if(num3 > 5) // 반올림
                num2+=1;

            if(num2 == 0) // 2000, 2020 -> 2km
                return String.format("%dkm", num1);

            return String.format("%d.%dkm", num1, num2);
        }
        else if (length > 4){
            distance = distance.substring(0, distance.length() - 3); // 맨 뒤 3자리 m 없애기
            return String.format("%skm", distance);
        }

        return "unknown";
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
}
