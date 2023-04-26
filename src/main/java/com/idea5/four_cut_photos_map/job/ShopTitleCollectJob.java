package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopTitleCollectJob {
    private final FavoriteRepository favoriteRepository;
    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleLogService shopTitleLogService;

    @Scheduled(cron = "0 0 3 1 * *") // 매달 1일 3시에 실행
    @Transactional
    public void collectHotPlaceTitle() {
        log.info("=======Start Hot Place Title Collect Job=======");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        // 1. 칭호 부여 전 ShopTitleLog 테이블 데이터 전체 삭제
        shopTitleLogRepository.deleteAll();

        // 2. 저번 달 찜 개수 기준으로 이번 달 핫플레이스 칭호 부여
        List<Long> ids = favoriteRepository.findShopIdsWithMoreThanThreeFavorites(lastMonthStart, lastMonthEnd);

        for (long shopId : ids) {
            shopTitleLogService.save(shopId, HOT_PLACE.getId());
        }

        log.info("=======End Hot Place Title Collect Job=======");
    }
}
