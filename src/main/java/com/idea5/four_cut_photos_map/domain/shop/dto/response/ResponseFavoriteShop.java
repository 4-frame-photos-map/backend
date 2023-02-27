package com.idea5.four_cut_photos_map.domain.shop.dto.response;

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
    private String brand;
    private String placeName;
    private String roadAddressName;
    private Integer favoriteCnt;
}
