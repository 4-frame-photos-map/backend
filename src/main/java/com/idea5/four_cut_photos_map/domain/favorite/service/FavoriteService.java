package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitle.service.ShopTitleService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class FavoriteService {
    public static final int MAX_FAVORITE_SHOP_COUNT = 20;
    private final ShopService shopService;
    private final FavoriteRepository favoriteRepository;

    private final ShopTitleLogService shopTitleLogService;
    private final ShopTitleService shopTitleService;


    // 찜하기
    @Transactional
    public Shop save(Long shopId, Member member) {
        // 1. 중복 데이터 생성 불가 -> 기존 데이터 생성 여부 체크
        if(findByShopIdAndMemberId(shopId, member.getId()) != null){
            throw new BusinessException(DUPLICATE_FAVORITE);
        }

        // 2. 최대 찜 개수 초과 여부 체크
        if(countByMember(member) >= MAX_FAVORITE_SHOP_COUNT){
            throw new BusinessException(FAVORITE_LIMIT_EXCEEDED);
        }

        // 3. 저장
        Shop shop = shopService.findById(shopId);
        Favorite favorite = Favorite.builder()
                .member(member)
                .shop(shop)
                .build();
        favoriteRepository.save(favorite);

        return shop;
    }

    // 찜 취소
    @Transactional
    public void cancel(Long shopId, Long memberId) {
        // 1. 데이터 존재 여부 체크
        Favorite favorite = findByShopIdAndMemberId(shopId, memberId);
        if (favorite == null) {
            throw new BusinessException(DELETED_FAVORITE);
        }

        // 2. 삭제
        try {
            try {
                favoriteRepository.deleteByShopIdAndMemberId(shopId, memberId);
            } catch (ObjectOptimisticLockingFailureException oe) {
                log.info("===Retry to delete due to concurrency===");
                if (favoriteRepository.existsById(favorite.getId())) {
                    favoriteRepository.deleteByShopIdAndMemberId(shopId, memberId);
                } else {
                    throw oe;
                }
            }
        } catch (Exception e) {
            throw new BusinessException(DELETED_FAVORITE);
        }
    }

    // 찜 목록 조회
    public List<FavoriteResponse> getFavoritesList(Long memberId, Double longitude, Double latitude) {
        List<Favorite> favorites = favoriteRepository.findByMemberIdOrderByCreateDateDesc(memberId);

        return  favorites
                .stream()
                .map(favorite -> shopService.renameShopAndSetResponseDto(favorite, longitude, latitude))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }

    // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//    @Transactional
//    public boolean isHotPlace(Long shopId) {
//        // Favorite DB에 저장된 Shop 찾기
//        List<Favorite> list = favoriteRepository.findByShopId(shopId);
//
//        // 찜수가 5개 이상이면 칭호부여
//        if (list.size() >= 5) {
//            shopTitleLogService.save(shopId, HOT_PLACE.getId());
//            return true;
//        }
//
//        return false;
//    }

    public void deleteByMemberId(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMember(Member.builder().id(memberId).build());
        for(Favorite favorite : favorites) {
            favoriteRepository.delete(favorite);
        }
    }

    // 해당 회원의 찜 개수
    public Long countByMember(Member member) {
        return favoriteRepository.countByMember(member);
    }
}

