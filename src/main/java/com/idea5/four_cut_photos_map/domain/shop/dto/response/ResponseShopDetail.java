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
    private Long id;
    private String placeName;
    private String roadAddressName;
    private String distance;
    private String placeUrl;
    private boolean canBeAddedToFavorites;

//    @JsonIgnore // 상점이 보유한 칭호가 없다면 null 보다는 응답 데이터에서 제외되는게 더 낫다고 생각
    private List<String> shopTitles = new ArrayList<>();

        // todo : Review 추가;
    public static ResponseShopDetail of(Shop dbShop, String distance, String placeUrl){
        return ResponseShopDetail.builder()
                .id(dbShop.getId())
                .placeName(dbShop.getPlaceName())
                .roadAddressName(dbShop.getRoadAddressName())
                .distance(distance)
                .placeUrl(placeUrl)
                .build();
    }

    public void setShopTitles(List<String> shopTitles) {
        this.shopTitles = shopTitles;
    }
}