package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import lombok.Data;
import lombok.Getter;

@Getter
public class KaKaoSearchResponseDto {
    public Document[] documents;

    @Data
    public static class Document {
        public String place_name; // 장소명
        public String road_address_name; // 도로명 주소
        public String x; // 경도
        public String y; // 위도
        public String distance; // 중심좌표까지의 거리 (단, x,y 파라미터를 준 경우에만 존재)
    }
}