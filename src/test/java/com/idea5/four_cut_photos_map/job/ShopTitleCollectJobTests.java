package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;
import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("저번 달 찜 개수가 3개 이상이면 핫플레이스 칭호를 부여한다.")
    @Test
    @Rollback(value = false)
    void collectHotPlaceTitle() {
        // given
        Member memberA = memberRepository.save(new Member());
        Member memberB = memberRepository.save(new Member());
        Member memberC = memberRepository.save(new Member());

        ShopTitle shopTitle = new ShopTitle(HOT_PLACE.getName(), HOT_PLACE.getConditions(), HOT_PLACE.getContent());
        shopTitleRepository.save(shopTitle);

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand1, "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 0, 0, 0.0));

        favoriteService.save(shop1.getId(), memberA);
        favoriteService.save(shop1.getId(), memberB);
        favoriteService.save(shop1.getId(), memberC);
        favoriteService.save(shop2.getId(), memberA);
        favoriteService.save(shop2.getId(), memberB);
        favoriteService.save(shop2.getId(), memberC);

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
}
