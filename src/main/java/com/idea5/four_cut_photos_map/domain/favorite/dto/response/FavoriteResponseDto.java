package com.idea5.four_cut_photos_map.domain.favorite.dto.response;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
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

    public static FavoriteResponseDto from(Favorite favorite) {
        ResponseFavoriteShop shopDto = ResponseFavoriteShop.from(favorite.getShop());

        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .shop(shopDto)
                .build();
    }

}
