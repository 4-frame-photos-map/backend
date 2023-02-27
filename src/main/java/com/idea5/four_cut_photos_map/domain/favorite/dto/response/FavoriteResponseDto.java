package com.idea5.four_cut_photos_map.domain.favorite.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseFavoriteShop;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {
        private Long id;
        private ResponseFavoriteShop shop;

        public static ResponseFavoriteShop from(Shop shop) {
                return ResponseFavoriteShop.builder()
                        .id(shop.getId())
                        .brand(shop.getBrand())
                        .placeName(shop.getPlaceName())
                        .roadAddressName(shop.getRoadAddressName())
                        .favoriteCnt(shop.getFavoriteCnt())
                        .build();
        }
}
