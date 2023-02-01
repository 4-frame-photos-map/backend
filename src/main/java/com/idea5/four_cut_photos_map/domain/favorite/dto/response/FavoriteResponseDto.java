package com.idea5.four_cut_photos_map.domain.favorite.dto.response;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {
        private Long id;
        private Member member;
        private Shop shop;
}
