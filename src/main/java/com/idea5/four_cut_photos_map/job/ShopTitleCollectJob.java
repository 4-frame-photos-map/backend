package com.idea5.four_cut_photos_map.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopTitleCollectJob {
    private final HotPlaceTitleService hotPlaceTitleService;
    private final GoodCleanlinessTitleService goodCleanlinessTitleService;
    @Scheduled(cron = "0 0 3 1 * *") // 매달 1일 3시에 실행
    public void collectShopTitle() {
        hotPlaceTitleService.collectHotPlaceTitles();
        goodCleanlinessTitleService.collectGoodCleanlinessTitles();
    }
}
