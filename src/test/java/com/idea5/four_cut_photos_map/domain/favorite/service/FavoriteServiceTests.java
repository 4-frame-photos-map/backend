package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DELETED_FAVORITE;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DUPLICATE_FAVORITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FavoriteServiceTests {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void createSampleData(){
        Member member = new Member();
        memberRepository.save(member);

        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));

        Shop shop1 = new Shop(brand1, "인생네컷 카페성수로드점", "서울 성동구 서울숲4길 13",0,0,0.0);
        Shop shop2 = new Shop(brand1, "인생네컷 서울숲점", "서울 성동구 서울숲4길 20",0,0,0.0);
        Shop shop3 = new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2",0,0,0.0);

        shopRepository.save(shop1);
        shopRepository.save(shop2);
        shopRepository.save(shop3);

        favoriteRepository.save(new Favorite(member, shop1));
        favoriteRepository.save(new Favorite(member, shop2));
        favoriteRepository.save(new Favorite(member, shop3));
    }

    @AfterEach
    public void clearData(){
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("회원 찜 목록 조회, 최근 추가순으로 정렬")
    void t1() {
        // given
        long memberId = 1L;
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // when
        List<FavoriteResponse> favoriteResponseDtos = favoriteService.getFavoritesList(memberId, userLat, userLng);

        // then
        assertAll(
                () -> assertThat(favoriteResponseDtos).isNotEmpty(),
                () -> assertThat(favoriteResponseDtos.size()).isEqualTo(3),
                () -> assertThat(favoriteResponseDtos.get(0).getShop().getPlaceName()).isEqualTo("포토이즘박스 성수점"));
    }

    @Test
    @DisplayName("찜 추가")
    void t2() {
        // given
        Member member = memberRepository.findById(1L).get();
        Brand brand = brandRepository.findById(1L).get();
        Shop shop = new Shop(brand, "인생네컷 서울이태원점", "서울 용산구 이태원로 171",0,0,0.0);
        shopRepository.save(shop);

        // when
        favoriteService.save(shop.getId(), member);

        // then
        Favorite favorite = favoriteService.findByShopIdAndMemberId(shop.getId(), member.getId());
        assertAll(
                () -> assertThat(favorite).isNotNull(),
                () -> assertThat(favorite.getShop()).isEqualTo(shop),
                () -> assertThat(favorite.getMember()).isEqualTo(member));
    }

    @Test
    @DisplayName("이미 찜한 상점 찜 추가 시도")
    void t3() {
        // given
        Member member = memberRepository.findById(1L).get();
        Shop shop = shopRepository.findById(1L).get();

        // when
        Throwable throwable = assertThrows(Exception.class, () -> favoriteService.save(shop.getId(), member));

        // then
        assertTrue(throwable.getMessage().contains(DUPLICATE_FAVORITE.getMessage()));
    }

    @Test
    @DisplayName("찜 취소")
    void t4() {
        // given
        long shopId = 1L;
        long memberId = 1L;

        // when
        favoriteService.cancel(shopId, memberId);

        // then
        Favorite canceledFavorite = favoriteService.findByShopIdAndMemberId(shopId, memberId);
        assertAll(() -> assertThat(canceledFavorite).isNull());
    }

    @Test
    @DisplayName("이미 찜 취소한 상점 찜 취소 시도")
    void t5() {
        // given
        long shopId = 1L;
        long memberId = 1L;
        favoriteService.cancel(shopId, memberId);

        // when
        Throwable throwable = assertThrows(Exception.class, () -> favoriteService.cancel(shopId, memberId));

        // then
        assertTrue(throwable.getMessage().contains(DELETED_FAVORITE.getMessage()));
    }
}