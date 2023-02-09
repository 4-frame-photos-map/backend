package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ShopFavoritesResponseDto {
    private Long id;
    private String brand;
    private String name;
    private String address;
    private Integer favoriteCnt;
}
