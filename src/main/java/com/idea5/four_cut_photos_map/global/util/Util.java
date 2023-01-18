package com.idea5.four_cut_photos_map.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.AppConfig;

import java.util.LinkedHashMap;
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
}