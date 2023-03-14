package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitle.service.ShopTitleService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DELETED_FAVORITE;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DUPLICATE_FAVORITE;

@RequiredArgsConstructor
@Service
public class FavoriteService {
    private final ShopService shopService;
    private final FavoriteRepository favoriteRepository;

    private final ShopTitleLogService shopTitleLogService;
    private final ShopTitleService shopTitleService;


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
        shop.setFavoriteCnt(shop.getFavoriteCnt()+1);
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
        shop.setFavoriteCnt(shop.getFavoriteCnt() <= 0? 0 : shop.getFavoriteCnt() - 1);
    }

    public List<FavoriteResponseDto> getFavoritesList(Long memberId, String criteria) {
        return switch (criteria) {
            case "placename" -> findByMemberIdOrderByPlaceName(memberId);
            default -> findByMemberIdOrderByCreateDateDesc(memberId);
        };
    }
    public List<FavoriteResponseDto> findByMemberIdOrderByCreateDateDesc(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMemberIdOrderByCreateDateDesc(memberId).orElse(null);

        if(favorites.isEmpty()) {return null;}

        return  favorites
                .stream()
                .map(favorite -> FavoriteResponseDto.from(favorite))
                .collect(Collectors.toList());
    }

    public List<FavoriteResponseDto> findByMemberIdOrderByPlaceName(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMemberIdOrderByShop_PlaceName(memberId).orElse(null);

        if(favorites.isEmpty()) {return null;}

        return  favorites
                .stream()
                .map(favorite -> FavoriteResponseDto.from(favorite))
                .collect(Collectors.toList());
    }

        public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }

    @Transactional
    public boolean isHotPlace(Long shopId) {
        // Favorite DB에 저장된 Shop 찾기
        List<Favorite> list = favoriteRepository.findByShopId(shopId);

        // 찜수가 5개 이상이면 칭호부여
        if (list.size() >= 5) {
            shopTitleLogService.save(shopId, HOT_PLACE.getId());
            return true;
        }

        return false;
    }
    public void deleteByMemberId(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMember(Member.builder().id(memberId).build());
        for(Favorite favorite : favorites) {
            favoriteRepository.delete(favorite);
        }
    }
}

