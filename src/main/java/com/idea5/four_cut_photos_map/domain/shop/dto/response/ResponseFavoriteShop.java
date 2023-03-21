package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseFavoriteShop {
    private Long id;
    private String placeName;
    private String roadAddressName;

    public static ResponseFavoriteShop from(Shop shop) {
        return ResponseFavoriteShop.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .build();
    }
}
