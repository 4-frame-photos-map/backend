package com.idea5.four_cut_photos_map.domain.shop.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoKeywordResponseDto {
        public String placeName; // 장소명
        public String roadAddressName; // 도로명 주소
        public String longitude; // 경도
        public String latitude; // 위도
        public String distance; // 중심좌표까지의 거리
        public String phone; // todo: 필요없다면 제거 필요
}