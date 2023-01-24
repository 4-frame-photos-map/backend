package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class ResponseShop {

    private String place_name;// 장소명

    private String road_address_name; // 전체 도로명 주소
    private String x; // 위도
    private String y; // 경도
    private String distance; // 중심좌표까지의 거리

    public static ResponseShop from(Shop shop){
        return ResponseShop.builder()
                .place_name(shop.getName())
                .road_address_name(shop.getAddress())
                .x(String.valueOf(shop.getLatitude()))
                .y(String.valueOf(shop.getLongitude()))
                .build();
    }
    public void setDistance(String distance){
        this.distance = distance;
    }

}
