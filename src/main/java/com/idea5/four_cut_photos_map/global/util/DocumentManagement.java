package com.idea5.four_cut_photos_map.global.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DocumentManagement {
    private Document[] documents;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Document{
        private String place_name; // 장소명

        private String address_name; // 지번 주소
        private String road_address_name; // 도로명 주소
        private String x; // 경도
        private String y; // 위도
        private String distance; // 거리
        private String phone;
        public Document() {
        }
    }
}
