package com.idea5.four_cut_photos_map.domain.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {
        @JsonProperty("place_name")
        public String placeName; // 장소명
        @JsonProperty("road_address_name")
        public String roadAddressName; // 도로명 주소
        @JsonProperty("x")
        public String longitude; // 경도
        @JsonProperty("y")
        public String latitude; // 위도
        @JsonProperty("distance")
        public String distance; // 중심좌표까지의 거리
}