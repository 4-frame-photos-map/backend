package com.idea5.four_cut_photos_map.domain.shop.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String placeName;// 장소명
    private String roadAddressName; // 전체 도로명 주소
    private String distance; // 중심좌표까지의 거리
    private boolean canBeAddedToFavorites; // 사용자의 찜 여부
    private int favoriteCnt; // 찜 수

//    @JsonIgnore // 상점이 보유한 칭호가 없다면 null 보다는 응답 데이터에서 제외되는게 더 낫다고 생각
    private List<String> shopTitles = new ArrayList<>();

        // todo : Review 추가;
    public static ResponseShopDetail of(Shop shop, String distance){
        return ResponseShopDetail.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .distance(distance)
                .favoriteCnt(shop.getFavoriteCnt())
                .build();
    }

    public void setDistance(String distance){
        this.distance = distance;
    }

    public void setShopTitles(List<String> shopTitles) {
        this.shopTitles = shopTitles;
    }


}
