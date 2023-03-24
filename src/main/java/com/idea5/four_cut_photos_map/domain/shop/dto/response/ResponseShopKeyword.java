package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class ResponseShopKeyword {
    private long id;
    private String placeName;
    private String roadAddressName;
    private String latitude;
    private String longitude;
    private String distance;
    private boolean canBeAddedToFavorites;

    public static ResponseShopKeyword of(Shop shop, KakaoResponseDto apiShop){
        return ResponseShopKeyword.builder()
                .id(shop.getId())
                .placeName(apiShop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .build();
    }
}
