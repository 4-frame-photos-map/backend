package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoSearchResponseDto {
        public String place_name; // 장소명
        public String road_address_name; // 도로명 주소
        public String x; // 경도
        public String y; // 위도


}