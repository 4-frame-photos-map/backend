package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    // 찜하기
    @Transactional
    public Shop save(Long shopId, Member member) {
        // 1. 중복 데이터 생성 불가 -> 기존 데이터 생성 여부 체크
        if(favoriteRepository.existsByShopIdAndMemberId(shopId, member.getId())){
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
    @Retryable(
            value = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public Shop cancel(Long shopId, Long memberId) {
        Favorite favorite = favoriteRepository.findByShopIdAndMemberId(shopId, memberId)
                .orElseThrow(() -> new BusinessException(DELETED_FAVORITE));

        favoriteRepository.delete(favorite);
        // 여기서 flush를 하지 않아도, 커밋 시 실패하면 @Retryable이 트랜잭션을 새로 열어 재시도.
        return favorite.getShop();
    }

    // 테스트용 임의 메서드
    @Transactional
    public void updateTest(Long shopId, Long memberId) {
        Favorite favorite = favoriteRepository.findByShopIdAndMemberId(shopId, memberId)
                .orElseThrow(() -> new BusinessException(DELETED_FAVORITE));

        try {
            // 1. 상태 변경 (Dirty Checking 유발)
            // 실제 운영 환경의 '수정' 상황을 재현. (Favorite에 memo 같은 필드가 있다고 가정)
            favorite.setShop(shopService.findById(2L)); // 예시로 shop을 다시 set하는 식으로 변경사항을 만든다


            // 2. [핵심] 수정 이후에 'JPQL 기반 조회'를 실행
            // 하이버네이트는 "수정사항(Update)이 있으니 조회 전 Flush해야지!"라고 판단
            favoriteRepository.findAll();

        } catch (ObjectOptimisticLockingFailureException oe) {
            System.out.println("===Retry to UPDATE due to concurrency===");
            // 여기서 예외가 잡힌다면 가설 증명
        }
    }

    // 찜 목록 조회
    public List<FavoriteResponse> getFavoritesList(Long memberId, Double userLat, Double userLng) {
        List<Favorite> favorites = favoriteRepository.findByMemberIdOrderByCreateDateDesc(memberId);

        return  favorites
                .stream()
                .map(favorite -> shopService.setResponseDto(favorite, userLat, userLng))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Favorite findByShopIdAndMemberId(Long shopId, Long memberId) {
        return favoriteRepository.findByShopIdAndMemberId(shopId, memberId).orElse(null);
    }

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

