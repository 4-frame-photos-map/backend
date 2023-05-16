package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.GOOD_CLEANLINESS;
import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopTitleCollectJob {
    private final FavoriteRepository favoriteRepository;
    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleLogService shopTitleLogService;
    private final ReviewRepository reviewRepository;

//    @Scheduled(cron = "0 0 3 1 * *") // 매달 1일 3시에 실행
@Scheduled(cron = "0 25 * * * *")
@Transactional
    public void collectHotPlaceTitle() {
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

//    @Scheduled(cron = "0 0 3 1 * *") // 매달 1일 3시에 실행
@Scheduled(cron = "0 25 * * * *")
@Transactional
    public void collectGoodCleanlinessTitle() {
        log.info("=======Start Good Cleanliness Title Collect Job=======");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        // 1. 이전 달 ShopTitleLog 테이블 데이터 전체 삭제
        shopTitleLogRepository.deleteOldShopTitles(lastMonthStart.plusDays(1)); // 지난 달 2일 이전 생성된 데이터 제거

        // 2. 저번 달 리뷰 개수와 청결도 평균을 기준으로 이번 달 청결한 지점 칭호 부여
        List<Review> reviews = reviewRepository.findLastMonthReviewsWithGoodOrBadPurity(lastMonthStart, lastMonthEnd);

        Map<Long, List<Review>> reviewMap = reviews.stream()
                .collect(Collectors.groupingBy(review -> review.getShop().getId()));

        for (Map.Entry<Long, List<Review>> entry : reviewMap.entrySet()) {
            int reviewCnt = entry.getValue().size();
            if(reviewCnt >= 3) {
                int sum = 0;
                for (Review review : entry.getValue()) {
                    sum += review.getPurity().getValue();
                }
                double purityAvg = sum / reviewCnt;

                if (purityAvg >= 0.8) shopTitleLogService.save(entry.getKey(), GOOD_CLEANLINESS.getId());
            }
        }

        log.info("=======End Good Cleanliness Title Collect Job=======");
    }
}
