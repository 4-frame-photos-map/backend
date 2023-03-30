package com.idea5.four_cut_photos_map.domain.favorite.dto.response;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseFavoriteShop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FavoriteResponse {
    private Long id;
    private ResponseFavoriteShop shop;

    public static FavoriteResponse from(Favorite favorite) {
        ResponseFavoriteShop shopDto = ResponseFavoriteShop.from(favorite.getShop());

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .shop(shopDto)
                .build();
    }

}
