package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class ResponseShop {

    private String name;// 장소명

    private String address; // 전체 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리

    public static ResponseShop from(Shop shop){
        return ResponseShop.builder()
                .name(shop.getName())
                .address(shop.getAddress())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .build();
    }
    public void setDistance(String distance){
        this.distance = distance;
    }

}
