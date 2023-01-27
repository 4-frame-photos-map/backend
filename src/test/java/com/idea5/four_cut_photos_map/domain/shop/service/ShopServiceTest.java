package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.like.repository.LikeRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.common.data.TempKaKaO;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ShopServiceTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("상점 상세보기")
    @Test
    void getShopDetail() {
        // given
        Shop shop = Shop.builder()
                .id(1L)
                .brand("인생네컷")
                .name("인생네컷 강남점")
                .address("서울 강남구~")
                .latitude(100.1)
                .longitude(100.2)
                .build();
        shopRepository.save(shop);

        // when
        ResponseShopDetail shopDetail = shopService.findShopById(1L, "3km");

        // then
        assertAll(
                ()->assertThat(shopDetail.getName()).isEqualTo("인생네컷 강남점"),
                ()->assertThat(shopDetail.getAddress()).isEqualTo("서울 강남구~"),
                ()->assertThat(shopDetail.getLatitude()).isEqualTo(100.1),
                ()->assertThat(shopDetail.getLongitude()).isEqualTo(100.2)
        );
    }

    // todo : apiResponse, db 비교 (팀원과 의논)
    @DisplayName("text로 Shop 검색")
    @Test
    void findShopsByTextKeyword() {
        // given

        // when

        // then
    }

    // todo : 브랜드 키워드 검색 ( KaKaoAPi 적용 후, 리팩토링 필요 )
    @DisplayName("Brand 명으로 검색")
    @Test
    void findShopByKeyword() {
        // given
        String keyword = "인생네컷";
        List<ShopDto> apiShops = TempKaKaO.tempDataBySearch(keyword); // todo : KaKaoApi 적용 후, 수정

        shopRepository.save(new Shop("인생네컷", "인생네컷 카페성수로드점", "서울 강남구~", 100.1, 100.1));
        shopRepository.save(new Shop("인생네컷", "인생네컷 홍대점", "서울 마포구~", 100.1, 100.1));
        shopRepository.save(new Shop("인생네컷", "인생네컷 신림점", "서울 강남구~", 100.1, 100.1));
        shopRepository.save(new Shop("하루필름", "하루필름 홍대점", "서울 강남구~", 100.2, 100.2));
        shopRepository.save(new Shop("포토이즘", "포토이즘 홍대점", "서울 강남구~", 100.3, 100.3));
        shopRepository.save(new Shop("포토그레이", "포토그레이 홍대점", "서울 강남구~", 100.4, 100.4));
        shopRepository.save(new Shop("포토시그니처", "포토시그니처 홍대점", "서울 강남구~", 100.5, 100.5));
        shopRepository.save(new Shop("비룸", "비룸 홍대점", "서울 ~", 100.6, 100.6));
        shopRepository.save(new Shop("포토드링크", "포토드링크 홍대점", "서울 강남구~", 100.7, 100.7));
        shopRepository.save(new Shop("포토매틱", "포토매틱 홍대점", "서울 강남구~", 100.8, 100.8));
        shopRepository.save(new Shop("셀픽스", "셀픽스 홍대점", "서울 강남구~", 100.9, 100.9));

        // when
        List<ResponseShop> shops = shopService.findShopsByBrand(apiShops, keyword);
        // then
        assertAll(
                () -> assertThat(shops.size()).isEqualTo(1)
        );
    }

}