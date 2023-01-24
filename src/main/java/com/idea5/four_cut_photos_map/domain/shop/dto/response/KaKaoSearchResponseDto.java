package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class KaKaoSearchResponseDto {
    public Document[] documents;


    @Builder
    @Getter
    public static class Document {
        public String place_name; // 장소명
        public String road_address_name; // 도로명 주소
        public String x; // 경도
        public String y; // 위도
        public String distance; // 중심좌표까지의 거리 (단, x,y 파라미터를 준 경우에만 존재)

        public Document() {
        }

        public Document(String place_name, String road_address_name, String x, String y, String distance) {
            this.place_name = place_name;
            this.road_address_name = road_address_name;
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

}