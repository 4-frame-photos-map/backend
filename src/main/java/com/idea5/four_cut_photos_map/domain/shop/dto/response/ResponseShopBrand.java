package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShopBrand {
    private String placeName; // 장소명
    private String roadAddressName; // 도로명 주소
    private String x; // 경도
    private String y; // 위도
    private String distance; // 거리

    private String phone;

    public void setDistance(String distance){
        this.distance = distance;
    }

    static public ResponseShopBrand of(KakaoResponseDto dto){
        return ResponseShopBrand.builder()
                .placeName(dto.getPlaceName())
                .roadAddressName(dto.getRoadAddressName())
                .phone(dto.getPhone())
                .x(dto.getX())
                .y(dto.getY())
                .distance(dto.getDistance())
                .build();
    }

    public static ResponseShopBrand from(Shop shop){
        return ResponseShopBrand.builder()
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .build();
    }
}