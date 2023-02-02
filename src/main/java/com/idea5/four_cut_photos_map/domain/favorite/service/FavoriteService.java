package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final ShopService shopService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteResponseDto toDto(Favorite favorite) {

        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .member(favorite.getMember())
                .shop(favorite.getShop())
                .build();
    }

    @Transactional
    public void save(Long shopId, Member member) {
        Shop shop = shopService.findById(shopId);

        Favorite favorite = Favorite.builder()
                .member(member)
                .shop(shop)
                .build();

        favoriteRepository.save(favorite);
    }

    public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }

    public void cancel(Long shopId, Long memberId) {
        favoriteRepository.deleteByShopIdAndMemberId(shopId, memberId);
    }
}
