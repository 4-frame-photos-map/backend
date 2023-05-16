package com.idea5.four_cut_photos_map.domain.shoptitlelog.dto;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseFavoriteShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@AllArgsConstructor
@Getter
@Builder
public class ShopTitleLogDto {
    private ResponseShopTitleLog shop;
    private ShopTitle shopTitle;

    static public ShopTitleLogDto of (ShopTitleLog shopTitleLog){
        ResponseShopTitleLog shopDto = ResponseShopTitleLog.from(shopTitleLog.getShop());

        return ShopTitleLogDto.builder()
                .shop(shopDto)
                .shopTitle(shopTitleLog.getShopTitle())
                .build();
    }
}
