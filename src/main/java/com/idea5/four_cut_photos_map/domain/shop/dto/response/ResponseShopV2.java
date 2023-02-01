package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShopV2 {
    private String placeName; // 장소명
    private String roadAddressName; // 도로명 주소
    private String x; // 경도
    private String y; // 위도
    private String phone; // 번호
    private String distance; // 거리

    public void setDistance(String distance){
        this.distance = distance;
    }

    static public ResponseShopV2 ofKakaoResponse(KakaoResponseDto dto){
        return ResponseShopV2.builder()
                .placeName(dto.getPlaceName())
                .roadAddressName(dto.getRoadAddressName())
                .phone(dto.getPhone())
                .x(dto.getX())
                .y(dto.getY())
                .distance(dto.getDistance())
                .build();
    }

    public static ResponseShopV2 of(Shop shop){
        return ResponseShopV2.builder()
                .placeName(shop.getName())
                .roadAddressName(shop.getAddress())
                .x(String.valueOf(shop.getLongitude()))
                .y(String.valueOf(shop.getLatitude()))
                .build();
    }
}
