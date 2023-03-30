package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 카카오맵 API 공통 응답 DTO (키워드 조회, 전체/브랜드별 조회)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMapSearchDto {
        @JsonProperty("place_name")
        public String placeName;
        @JsonProperty("road_address_name")
        public String roadAddressName;
        @JsonProperty("x")
        public String longitude;
        @JsonProperty("y")
        public String latitude;
        @JsonProperty("distance")
        public String distance;
        @JsonProperty("place_url")
        public String placeUrl;
}