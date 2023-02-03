package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberFavoritesResp;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ShopFavoritesResponseDto;
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
    private final MemberService memberService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteResponseDto toDto(Favorite favorite) {
        MemberFavoritesResp memberDto = memberService.toMemberFavoritesRespDto(favorite.getMember());
        ShopFavoritesResponseDto shopDto = shopService.toShopFavoritesRespDto(favorite.getShop());

        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .member(memberDto)
                .shop(shopDto)
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

        // 찜 수 갱신
        shop.setFavorite_cnt(shop.getFavorite_cnt() == null? 1 : shop.getFavorite_cnt()+1);
    }

    public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }

    @Transactional
    public void cancel(Long shopId, Long memberId) {
        favoriteRepository.deleteByShopIdAndMemberId(shopId, memberId);

        // 찜 수 갱신
        Shop shop = shopService.findById(shopId);
        shop.setFavorite_cnt(shop.getFavorite_cnt() == null || shop.getFavorite_cnt() == 0?
                0 : shop.getFavorite_cnt() - 1);
    }
}
