package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ShopFavoritesResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DELETED_FAVORITE;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DUPLICATE_FAVORITE;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final ShopService shopService;
    private final FavoriteRepository favoriteRepository;

    // DTO 변환
    public FavoriteResponseDto toDto(Favorite favorite) {
        ShopFavoritesResponseDto shopDto = shopService.toShopFavoritesRespDto(favorite.getShop());

        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .ownerId(favorite.getMember().getId())
                .ownerNickname(favorite.getMember().getNickname())
                .shop(shopDto)
                .build();
    }

    // 찜하기
    @Transactional
    public void save(Long shopId, Member member) {
        // 1. 중복 데이터 생성 불가 -> 기존 데이터 생성 여부 체크
        if(findByShopIdAndMemberId(shopId, member.getId()) != null){
            throw new BusinessException(DUPLICATE_FAVORITE);
        }

        // 2. 저장
        Shop shop = shopService.findById(shopId);
        Favorite favorite = Favorite.builder()
                .member(member)
                .shop(shop)
                .build();
        favoriteRepository.save(favorite);

        // 3. shop 찜 수 갱신
        shop.setFavorite_cnt(shop.getFavorite_cnt() == null? 1 : shop.getFavorite_cnt()+1);
    }

    // 찜 취소
    @Transactional
    public void cancel(Long shopId, Long memberId) {

        // 1. 데이터 생성 여부 체크
        if(findByShopIdAndMemberId(shopId, memberId) == null){
            throw new BusinessException(DELETED_FAVORITE);
        }

        // 2. 삭제
        favoriteRepository.deleteByShopIdAndMemberId(shopId, memberId);

        // 3. shop 찜 수 갱신
        Shop shop = shopService.findById(shopId);
        shop.setFavorite_cnt(shop.getFavorite_cnt() == null || shop.getFavorite_cnt() == 0?
                0 : shop.getFavorite_cnt() - 1);
    }

    public List<Favorite> findByMemberId(Long memberId) {
        return favoriteRepository.findByMemberId(memberId).orElse(null);
    }

    public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }
}