package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.GOOD_CLEANLINESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodCleanlinessTitleService {
    private final ReviewRepository reviewRepository;
    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleLogService shopTitleLogService;

    @Transactional
    public void collectGoodCleanlinessTitles() {
        log.info("=======Start Good Cleanliness Title Collect Job=======");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
        LocalDateTime lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        // 1. 저번 달 리뷰 개수와 청결도 평균을 기준으로 이번 달 청결한 지점 칭호 부여
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
