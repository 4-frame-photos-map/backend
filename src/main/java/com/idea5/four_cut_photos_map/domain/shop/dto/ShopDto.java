package com.idea5.four_cut_photos_map.domain.shop.dto;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
/**
 * 브랜드별 Map Marker DTO (현재 미사용)
 */
@AllArgsConstructor
@Getter
@Builder
public class ShopDto {

    private Long id;
    private String placeName;// 장소명
    private String address; // 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리


    public ShopDto(String placeName, String address, double latitude, double longitude, String distance) {
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public static ShopDto of(Shop shop){
        return ShopDto.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .address(shop.getAddress())
                .build();
    }

}
