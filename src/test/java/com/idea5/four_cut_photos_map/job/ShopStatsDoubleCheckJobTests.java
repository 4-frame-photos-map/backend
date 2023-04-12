package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ShopStatsDoubleCheckJobTests {

    @Autowired
    private ShopStatsDoubleCheckJob doubleCheckJob;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void after() {
        databaseCleaner.execute();
    }

    @DisplayName("리뷰만 업데이트, 한 달 동안 리뷰만 추가 및 수정된 경우")
    @Test
    void t1() {
        // given
        Member member = memberRepository.save(new Member());

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        reviewRepository.save(new Review(member, shop1, 3, "좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.BAD));
        reviewRepository.save(new Review(member, shop1, 5, "재방문! 만족!", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member, shop2, 5, "소품 다양해서 좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member, shop2, 4, "재방문!", PurityScore.GOOD, RetouchScore.BAD, ItemScore.GOOD));
        reviewRepository.save(new Review(member, shop2, 4, "가까워서 자주 가요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.GOOD));

        // when
        //doubleCheckJob.updateFavoriteCnt();
        doubleCheckJob.updateReviewStats();

        // then
        Shop updatedShop1 = shopRepository.findById(1L).get();

        // 변경이 있는 컬럼만 업데이트됐는지 확인
        // 전체 업데이트가 됐다면 인수가 주어지지 않은 placeName null로 업데이트됨
        assertThat(updatedShop1.getPlaceName()).isNotNull();
        assertThat(updatedShop1.getReviewCnt()).isEqualTo(2);
        assertThat(updatedShop1.getStarRatingAvg()).isEqualTo(4.0);

        Shop updatedShop2 = shopRepository.findById(2L).get();

        assertThat(updatedShop2.getPlaceName()).isNotNull();
        assertThat(updatedShop2.getReviewCnt()).isEqualTo(3);
        assertThat(updatedShop2.getStarRatingAvg()).isEqualTo(4.3);
    }

    @DisplayName("찜만 업데이트, 한 달 동안 찜만 추가된 경우")
    @Test
    void t2() {
        // given
        Member member1 = memberRepository.save(new Member());
        Member member2 = memberRepository.save(new Member());

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        favoriteRepository.save(new Favorite(member1, shop1));
        favoriteRepository.save(new Favorite(member2, shop1));
        favoriteRepository.save(new Favorite(member2, shop2));

        // when
        doubleCheckJob.updateFavoriteCnt();
        //doubleCheckJob.updateReviewStats();

        // then
        Shop updatedShop1 = shopRepository.findById(1L).get();

        // 변경이 있는 컬럼만 업데이트됐는지 확인
        // 전체 업데이트가 됐다면 인수가 주어지지 않은 placeName null로 업데이트됨
        assertThat(updatedShop1.getPlaceName()).isNotNull();
        assertThat(updatedShop1.getFavoriteCnt()).isEqualTo(2);

        Shop updatedShop2 = shopRepository.findById(2L).get();

        assertThat(updatedShop2.getPlaceName()).isNotNull();
        assertThat(updatedShop2.getFavoriteCnt()).isEqualTo(1);
    }

    @DisplayName("찜과 리뷰 모두 업데이트")
    @Test
    void t3() {
        // given
        Member member1 = memberRepository.save(new Member());
        Member member2 = memberRepository.save(new Member());

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        reviewRepository.save(new Review(member1, shop1, 3, "좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.BAD));
        reviewRepository.save(new Review(member1, shop1, 5, "재방문! 만족!", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member1, shop2, 5, "소품 다양해서 좋아요", PurityScore.GOOD, RetouchScore.GOOD, ItemScore.GOOD));
        reviewRepository.save(new Review(member1, shop2, 4, "재방문!", PurityScore.GOOD, RetouchScore.BAD, ItemScore.GOOD));
        reviewRepository.save(new Review(member2, shop2, 4, "가까워서 자주 가요", PurityScore.GOOD, RetouchScore.UNSELECTED, ItemScore.GOOD));

        favoriteRepository.save(new Favorite(member1, shop1));
        favoriteRepository.save(new Favorite(member2, shop1));
        favoriteRepository.save(new Favorite(member2, shop2));

        // when
        doubleCheckJob.updateFavoriteCnt();
        doubleCheckJob.updateReviewStats();

        // then
        Shop updatedShop1 = shopRepository.findById(1L).get();

        assertThat(updatedShop1.getFavoriteCnt()).isEqualTo(2);
        assertThat(updatedShop1.getReviewCnt()).isEqualTo(2);
        assertThat(updatedShop1.getStarRatingAvg()).isEqualTo(4.0);

        Shop updatedShop2 = shopRepository.findById(2L).get();

        assertThat(updatedShop2.getFavoriteCnt()).isEqualTo(1);
        assertThat(updatedShop2.getReviewCnt()).isEqualTo(3);
        assertThat(updatedShop2.getStarRatingAvg()).isEqualTo(4.3);
    }
}

