package com.idea5.four_cut_photos_map.domain.shoptitle.dto;

import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;

@AllArgsConstructor
@Getter
@Builder
public class ShopTitleDto {
    private String name; // 칭호명
    private String conditions; // 칭호 획득 조건
    private String content; // 칭호 부연 설명

    public static ShopTitleDto of(ShopTitle shopTitle) {
        return ShopTitleDto.builder()
                .name(shopTitle.getName())
                .conditions(shopTitle.getConditions())
                .content(shopTitle.getContent())
                .build();
    }
}
