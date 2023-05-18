package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotPlaceTitleService {
    private final FavoriteRepository favoriteRepository;
    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleLogService shopTitleLogService;

    @Transactional
    public void collectHotPlaceTitles() {
        log.info("=======Start Hot Place Title Collect Job=======");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        // 1. 이전 달 ShopTitleLog 테이블 데이터 전체 삭제
        shopTitleLogRepository.deleteOldShopTitles(lastMonthStart.plusDays(1)); // 지난 달 2일 이전 생성된 데이터 제거

        // 2. 저번 달 찜 개수 기준으로 이번 달 핫플레이스 칭호 부여
        List<Long> ids = favoriteRepository.findShopIdsWithMoreThanThreeFavorites(lastMonthStart, lastMonthEnd);

        for (long shopId : ids) {
            shopTitleLogService.save(shopId, HOT_PLACE.getId());
        }

        log.info("=======End Hot Place Title Collect Job=======");
    }
}