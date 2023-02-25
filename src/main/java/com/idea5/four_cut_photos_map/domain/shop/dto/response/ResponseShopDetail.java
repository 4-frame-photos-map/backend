package com.idea5.four_cut_photos_map.domain.shop.dto.response;


import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseShopDetail {

    private Long id; // PK
    private String name;// 장소명
    private String address; // 전체 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리
    private boolean canBeAddedToFavorites; // 사용자의 찜 여부 // Entity에 추가 X(Entity Manager 관리 범위에 속하지 X)
    private int favoriteCnt; // 찜 수

    private List<String> shopTitles = new ArrayList<>();

        // todo : Review 추가;
    public static ResponseShopDetail of(Shop shop, String distance){
        return ResponseShopDetail.builder()
                .id(shop.getId())
                .name(shop.getPlaceName())
                .address(shop.getRoadAddressName())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .distance(distance)
                .favoriteCnt(shop.getFavoriteCnt() == null ? 0 : shop.getFavoriteCnt())
                .build();
    }

    public void setDistance(String distance){
        this.distance = distance;
    }

    public void setShopTitles(List<String> shopTitles) {
        this.shopTitles = shopTitles;
    }


}
