package com.idea5.four_cut_photos_map.domain.shoptitle.dto;

import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ShopTitleDto {
    private String name;
    private String conditions;
    private String content;

    public static ShopTitleDto of(ShopTitle shopTitle) {
        return ShopTitleDto.builder()
                .name(shopTitle.getName())
                .conditions(shopTitle.getConditions())
                .content(shopTitle.getContent())
                .build();
    }
}
