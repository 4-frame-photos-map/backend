package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.GOOD_CLEANLINESS;
import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ShopTitleCollectJobTests {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShopTitleRepository shopTitleRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ShopTitleLogService shopTitleLogService;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ShopTitleLogRepository shopTitleLogRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void after() {
        databaseCleaner.execute();
    }


    @DisplayName("저번 달 찜 개수가 3개 이상이면 핫플레이스 칭호를 부여한다.")
    @Test
    void collectHotPlaceTitle() {
        // given
        Member member1 = memberRepository.save(new Member());
        Member member2 = memberRepository.save(new Member());
        Member member3 = memberRepository.save(new Member());

        // ** ShopTitle 먼저 저장 작업 필요
        ShopTitle shopTitle = new ShopTitle(HOT_PLACE.getName(), HOT_PLACE.getConditions(), HOT_PLACE.getContent());
        shopTitleRepository.save(shopTitle);

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        favoriteService.save(shop1.getId(), member1);
        favoriteService.save(shop1.getId(), member2);
        favoriteService.save(shop1.getId(), member3);

        favoriteService.save(shop2.getId(), member1);
        favoriteService.save(shop2.getId(), member2);
        favoriteService.save(shop2.getId(), member3);

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);

        List<Long> ids = favoriteRepository.findShopIdsWithMoreThanThreeFavorites(lastMonthStart, now);

        for (long shopId : ids) {
            shopTitleLogService.save(shopId, HOT_PLACE.getId());
        }
        // then
        ShopTitleLog shopTitleLog1 = shopTitleLogService.findShopTitleLog(shop1.getId(), HOT_PLACE.getId());
        ShopTitleLog shopTitleLog2 = shopTitleLogService.findShopTitleLog(shop1.getId(), HOT_PLACE.getId());

        assertThat(shopTitleLog1.getShopTitleName()).isEqualTo(HOT_PLACE.getName());
        assertThat(shopTitleLog2.getShopTitleName()).isEqualTo(HOT_PLACE.getName());
    }

    @DisplayName("저번 달 리뷰 개수 3개 이상, 평균 0.8 이상이면 청결한 지점 칭호를 부여한다.")
    @Test
    void collectGoodCleanlinessTitle() {
        // given
        Member member1 = memberRepository.save(new Member());
        Member member2 = memberRepository.save(new Member());
        Member member3 = memberRepository.save(new Member());

        // ** ShopTitle 먼저 저장 작업 필요
        // ShopTitleType에 설계된 GOOD_CLEANLINESS id가 2L이기 때문에 테스트 시 1L에 해당하는 HOT_PLACE도 저장 필요
        ShopTitle shopTitleA = new ShopTitle(HOT_PLACE.getName(), HOT_PLACE.getConditions(), HOT_PLACE.getContent());
        ShopTitle shopTitleB = new ShopTitle(GOOD_CLEANLINESS.getName(), GOOD_CLEANLINESS.getConditions(), GOOD_CLEANLINESS.getContent());

        shopTitleRepository.save(shopTitleA);
        shopTitleRepository.save(shopTitleB);

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        // shop1 review (purity avg 0.75)
        reviewRepository.save(new Review(member1, shop1, 3, "좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.BAD));
        reviewRepository.save(new Review(member1, shop1, 5, "재방문! 만족!", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member2, shop1, 5, "소품 다양해서 좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member3, shop1, 5, "보통이에요", PurityScore.BAD, RetouchScore.GOOD, ItemScore.GOOD));

        // shop2 review (purity avg 1.0)
        reviewRepository.save(new Review(member1, shop2, 4, "자연스럽게 잘 나와요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member2, shop2, 4, "가까워서 자주 가요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.GOOD));
        reviewRepository.save(new Review(member3, shop2, 4, "매장이 깨끗해요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.UNSELECTED));

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);

        List<Review> reviews = reviewRepository.findLastMonthReviewsWithGoodOrBadPurity(lastMonthStart, now);

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

        // then
        ShopTitleLog shopTitleLog1 = shopTitleLogService.findShopTitleLog(shop1.getId(), GOOD_CLEANLINESS.getId());
        ShopTitleLog shopTitleLog2 = shopTitleLogService.findShopTitleLog(shop2.getId(), GOOD_CLEANLINESS.getId());

        assertThat(shopTitleLog1).isNull();
        assertThat(shopTitleLog2.getShopTitleName()).isEqualTo(GOOD_CLEANLINESS.getName());
    }

    @DisplayName("핫플레이스 칭호와 청결한 지점 칭호 부여 작업을 연속해서 진행")
    @Test
    void collectHotPlaceAndGoodCleanlinessTitle() {
        // given
        Member member1 = memberRepository.save(new Member());
        Member member2 = memberRepository.save(new Member());
        Member member3 = memberRepository.save(new Member());

        ShopTitle shopTitleA = new ShopTitle(HOT_PLACE.getName(), HOT_PLACE.getConditions(), HOT_PLACE.getContent());
        ShopTitle shopTitleB = new ShopTitle(GOOD_CLEANLINESS.getName(), GOOD_CLEANLINESS.getConditions(), GOOD_CLEANLINESS.getContent());

        shopTitleRepository.save(shopTitleA);
        shopTitleRepository.save(shopTitleB);

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        // shop1 favorite
        favoriteService.save(shop1.getId(), member1);
        favoriteService.save(shop1.getId(), member2);
        favoriteService.save(shop1.getId(), member3);

        // shop2 favorite
        favoriteService.save(shop2.getId(), member1);
        favoriteService.save(shop2.getId(), member2);
        favoriteService.save(shop2.getId(), member3);

        // shop1 review (purity avg 0.75)
        reviewRepository.save(new Review(member1, shop1, 3, "좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.BAD));
        reviewRepository.save(new Review(member1, shop1, 5, "재방문! 만족!", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member2, shop1, 5, "소품 다양해서 좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member3, shop1, 5, "보통이에요", PurityScore.BAD, RetouchScore.GOOD, ItemScore.GOOD));

        // shop2 review (purity avg 1.0)
        reviewRepository.save(new Review(member1, shop2, 4, "자연스럽게 잘 나와요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member2, shop2, 4, "가까워서 자주 가요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.GOOD));
        reviewRepository.save(new Review(member3, shop2, 4, "매장이 깨끗해요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.UNSELECTED));

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);

        // 핫플레이스 칭호 부여
        shopTitleLogRepository.deleteOldShopTitles(lastMonthStart.plusDays(1));

        List<Long> ids = favoriteRepository.findShopIdsWithMoreThanThreeFavorites(lastMonthStart, now);

        for (long shopId : ids) {
            shopTitleLogService.save(shopId, HOT_PLACE.getId());
        }

        // 청결한 지점 칭호 부여
        shopTitleLogRepository.deleteOldShopTitles(lastMonthStart.plusDays(1));

        List<Review> reviews = reviewRepository.findLastMonthReviewsWithGoodOrBadPurity(lastMonthStart, now);

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
        // then
        List<ShopTitleDto> shopTitleDto1 = shopTitleLogService.findShopTitlesByShopId(shop1.getId());
        List<ShopTitleDto> shopTitleDto2 = shopTitleLogService.findShopTitlesByShopId(shop2.getId());

        assertThat(shopTitleDto1.size()).isEqualTo(1);
        assertThat(shopTitleDto1.get(0).getName()).isEqualTo(HOT_PLACE.getName());

        assertThat(shopTitleDto2.size()).isEqualTo(2);
        assertThat(shopTitleDto2.get(0).getName()).isEqualTo(HOT_PLACE.getName());
        assertThat(shopTitleDto2.get(1).getName()).isEqualTo(GOOD_CLEANLINESS.getName());
    }
}
