package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopStatsDoubleCheckJob {
    private final ShopRepository shopRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    @Scheduled(cron = "0 0 0 1 * *") // 매달 1일에 실행
    @Transactional
    public void updateFavoriteCnt() {
        log.info("=======Start Favorite Double Check Job=======");

        // 1. 한 달 전부터 현재까지 추가된 Favorite 조회
        List<Favorite> favorites = favoriteRepository.findByCreateDateAfter(LocalDate.now().minusMonths(1).atStartOfDay().minusSeconds(1));

        // 2. Shop 별로 Favorite 그룹화
        Map<Long, List<Favorite>> favoriteMap = favorites.stream()
                .collect(Collectors.groupingBy(favorite -> favorite.getShop().getId()));

        // 3. 각 Shop의 찜 수 업데이트
        for (Map.Entry<Long, List<Favorite>> entry : favoriteMap.entrySet()) {
            int favoriteCnt = entry.getValue().size();

            Shop shop = shopRepository.findById(entry.getKey()).get();
            shop.setFavoriteCnt(favoriteCnt);
        }
        log.info("=======End Favorite Double Check Job=======");
    }

    @Scheduled(cron = "0 0 0 1 * *") // 매달 1일에 실행
    @Transactional
    public void updateReviewStats() {
        log.info("=======Start Review Stats Double Check Job=======");

        // 1. 한 달 전부터 현재까지 추가/수정된 Review 조회
        List<Review> reviews = reviewRepository.findByModifyDateAfter(LocalDate.now().minusMonths(1).atStartOfDay().minusSeconds(1));

        // 2. Shop 별로 Review 그룹화
        Map<Long, List<Review>> reviewMap = reviews.stream()
                .collect(Collectors.groupingBy(review -> review.getShop().getId()));

        // 3. 각 Shop의 리뷰 수, 평점 업데이트
        for (Map.Entry<Long, List<Review>> entry : reviewMap.entrySet()) {
            List<Review> reviewList = entry.getValue();
            int reviewCnt = reviewList.size();
            double sum = 0.0;
            for (Review review : reviewList) {
                sum += review.getStarRating();
            }
            double starRatingAvg = sum / reviewCnt;
            starRatingAvg = Double.parseDouble(String.format("%.1f", starRatingAvg));

            Shop shop = shopRepository.findById(entry.getKey()).get();
            shop.setReviewCnt(reviewCnt);
            shop.setStarRatingAvg(starRatingAvg);
        }
        log.info("=======End Review Stats Double Check Job=======");
    }

}
