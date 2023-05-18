package com.idea5.four_cut_photos_map.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopTitleCollectJob {
    private final HotPlaceTitleService hotPlaceTitleService;
    private final GoodCleanlinessTitleService goodCleanlinessTitleService;
    @Scheduled(cron = "0 0 3 1 * *") // 매달 1일 3시에 실행
    public void collectShopTitle() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        hotPlaceTitleService.collectHotPlaceTitles(lastMonthStart, lastMonthEnd);
        goodCleanlinessTitleService.collectGoodCleanlinessTitles(lastMonthStart, lastMonthEnd);
    }
}
