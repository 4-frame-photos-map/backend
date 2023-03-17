package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FavoriteServiceTests {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    DatabaseCleaner databaseCleaner;


    @BeforeEach
    void beforeEach(){
        clearData();
        createSampleData();
    }

    public void createSampleData(){
        Member member = new Member();
        memberRepository.save(member);

        Shop shop1 = new Shop("포토이즘박스 망원점", "서울 마포구 포은로 88", 0);
        Shop shop2 = new Shop("인생네컷 홍대동교점", "서울 마포구 홍익로6길 21",0);
        Shop shop3 = new Shop("하루필름 연남점", "서울 마포구 동교로46길 40", 0);

        shopRepository.save(shop1);
        shopRepository.save(shop2);
        shopRepository.save(shop3);

        favoriteRepository.save(new Favorite(member,shop1));
        favoriteRepository.save(new Favorite(member,shop2));
        favoriteRepository.save(new Favorite(member,shop3));
    }

    public void clearData(){
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("회원 찜 목록 조회, 최근 추가순으로 정렬")
    void t1() {
        // given
        long memberId = 1;
        String criteria = "created";
        // when
        List<FavoriteResponseDto> favoriteResponseDtos = favoriteService.getFavoritesList(memberId,criteria);

        // then
        assertAll(
                () -> assertThat(favoriteResponseDtos).isNotNull(),
                () -> assertThat(favoriteResponseDtos).isNotEmpty(),
                () -> assertThat(favoriteResponseDtos.size()).isEqualTo(3),
                () -> assertThat(favoriteResponseDtos.get(0).getShop().getPlaceName()).isEqualTo("하루필름 연남점"),
                () -> assertThat(favoriteResponseDtos.get(0).getShop().getRoadAddressName()).isEqualTo("서울 마포구 동교로46길 40"));
    }

    @Test
    @DisplayName("회원 찜 목록 조회, 장소명(오름차순)으로 정렬")
    void t2() {
        // given
        long memberId = 1;
        String criteria = "placename";
        // when
        List<FavoriteResponseDto> favoriteResponseDtos = favoriteService.getFavoritesList(memberId,criteria);

        // then
        assertAll(
                () -> assertThat(favoriteResponseDtos).isNotNull(),
                () -> assertThat(favoriteResponseDtos).isNotEmpty(),
                () -> assertThat(favoriteResponseDtos.size()).isEqualTo(3),
                () -> assertThat(favoriteResponseDtos.get(0).getShop().getPlaceName()).isEqualTo("인생네컷 홍대동교점"),
                () -> assertThat(favoriteResponseDtos.get(0).getShop().getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21"));
    }

    @Test
    @DisplayName("찜 추가")
    void t3() {
        // given
        Member member = memberRepository.findById(1L).get();
        Shop testShop = new Shop("모노맨션 마곡점", "서울 강서구 마곡중앙6로 93",0);
        shopRepository.save(testShop);

        // when
        favoriteService.save(testShop.getId(),member);

        // then
        Favorite favorite = favoriteRepository.findByShopIdAndMemberId(testShop.getId(),member.getId()).get();
        assertAll(
                () -> assertThat(favorite).isNotNull(),
                () -> assertThat(favorite.getShop().getPlaceName()).isEqualTo("모노맨션 마곡점"),
                () -> assertThat(favorite.getShop().getFavoriteCnt()).isEqualTo(1),
                () -> assertThat(favorite.getMember().getId()).isEqualTo(1L));
    }

    @Test
    @DisplayName("찜 취소")
    void t4() {
        // given
        long shopId = 1L;
        long memberId = 1L;

        // when
        favoriteService.cancel(shopId,memberId);

        // then
        Shop canceledShop = shopRepository.findById(shopId).get();
        Favorite canceledFavorite = favoriteRepository.findByShopIdAndMemberId(shopId,memberId).orElse(null);
        assertAll(
                () -> assertThat(canceledShop.getFavoriteCnt()).isEqualTo(0),
                () -> assertThat(canceledFavorite).isNull());
    }
}
