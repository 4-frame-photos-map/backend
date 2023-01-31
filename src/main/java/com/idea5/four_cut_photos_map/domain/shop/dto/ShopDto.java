package com.idea5.four_cut_photos_map.domain.shop.dto;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ShopDto {

    private String brand; // 브랜드명
    private String name;// 장소명

    private String address; // 전체 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리
    public static ShopDto from(Shop shop){
        return ShopDto.builder()
                .name(shop.getName())
                .address(shop.getAddress())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .build();
    }
}
