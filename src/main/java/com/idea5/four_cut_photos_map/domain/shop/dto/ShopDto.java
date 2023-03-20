package com.idea5.four_cut_photos_map.domain.shop.dto;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ShopDto {

    private Long id;
    private String placeName;// 장소명
    private String roadAddressName; // 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리


    public ShopDto(String placeName, String roadAddressName, double latitude, double longitude, String distance) {
        this.placeName = placeName;
        this.roadAddressName = roadAddressName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public static ShopDto of(Shop shop){
        return ShopDto.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .build();
    }

}
