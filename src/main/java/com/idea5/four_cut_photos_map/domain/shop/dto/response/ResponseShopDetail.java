package com.idea5.four_cut_photos_map.domain.shop.dto.response;


import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ResponseShopDetail {

    private Long id; // PK
    private String name;// 장소명
    private String address; // 전체 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리

    // todo : Review, 찜 추가


    public static ResponseShopDetail of(Shop shop, String distance){
        return ResponseShopDetail.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .distance(distance)
                .build();
    }

    public void setDistance(String distance){
        this.distance = distance;
    }


    // todo : Review 추가;
}
