package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopKeyword;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    private BrandRepository brandRepository;

    @DisplayName("Kakao Maps API 장소 데이터와 일치하는 DB 데이터 조회하기, 같은 주소 내에 다른 지점이 있는 경우")
    @Test
    void t1() {
        // given
        Brand lifeFourCuts = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand haruFilm = brandRepository.save(new Brand(MajorBrand.HARUFILM.getBrandName(), MajorBrand.HARUFILM.getFilePath()));

        shopRepository.save(new Shop(lifeFourCuts, "포토이즘박스 충장로점", "광주 동구 충장로 99",0,0,0.0));
        shopRepository.save(new Shop(haruFilm, "포토이즘컬러드 충장로점", "광주 동구 충장로 99",0,0,0.0));

        List<KakaoMapSearchDto> apiShops = new ArrayList<>();

        KakaoMapSearchDto kakaoKeywordResponseDto1 = KakaoMapSearchDto.builder()
                .placeName("포토이즘박스 충장로점")
                .addressName("광주 동구 충장로1가 2-5")
                .roadAddressName("광주 동구 충장로 99")
                .longitude("126.91784824854518")
                .latitude("35.14751526756572")
                .placeUrl("http://place.map.kakao")
                .distance("")
                .build();
        KakaoMapSearchDto kakaoKeywordResponseDto2 = KakaoMapSearchDto.builder()
                .placeName("포토이즘컬러드 충장로점")
                .addressName("광주 동구 충장로1가 2-5")
                .roadAddressName("광주 동구 충장로 99")
                .longitude("126.917848248545")
                .latitude("35.1475152675657")
                .placeUrl("http://place.map.kakao")
                .distance("")
                .build();

        apiShops.add(kakaoKeywordResponseDto1);
        apiShops.add(kakaoKeywordResponseDto2);

        // when
        List<ResponseShopKeyword> response = shopService.findMatchingShops(apiShops, ResponseShopKeyword.class);


        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(2),
                () -> Assertions.assertThat(response).extracting(ResponseShopKeyword::getPlaceName, ResponseShopKeyword::getPlaceAddress,
                                ResponseShopKeyword::getLongitude, ResponseShopKeyword::getLatitude)
                        .contains(
                                tuple("포토이즘박스 충장로점","광주 동구 충장로 99", "126.91784824854518", "35.14751526756572"),
                                tuple("포토이즘컬러드 충장로점","광주 동구 충장로 99", "126.917848248545", "35.1475152675657")
                        )
        );
    }

    @DisplayName("Kakao Maps API 장소 데이터와 일치하는 DB 데이터 조회하기, 동일한 지점을 가리키는 데이터가 2개 이상 존재하는 경우")
    @Test
    void t2() {
        // given
        Brand brand = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));

        Shop targetShop = Shop.builder()
                .brand(brand)
                .placeName("인생네컷 광주 충장로직영점")
                .address("광주 동구 충장로 94-9")
                .favoriteCnt(0)
                .reviewCnt(0)
                .starRatingAvg(0.0)
                .build();
        shopRepository.save(targetShop);
        Shop duplicateAddressShop = Shop.builder()
                .brand(brand)
                .placeName("인생네컷 광주 충장로점")
                .address("광주 동구 충장로 94-9")
                .favoriteCnt(0)
                .reviewCnt(0)
                .starRatingAvg(0.0)
                .build();
        shopRepository.save(duplicateAddressShop);

        List<KakaoMapSearchDto> apiShops = new ArrayList<>();
        KakaoMapSearchDto kakaoKeywordResponseDto = KakaoMapSearchDto.builder()
                .placeName("인생네컷 광주충장로직영점")
                .addressName("광주 동구 충장로1가 32")
                .roadAddressName("광주 동구 충장로 94-9")
                .longitude("126.917159374681")
                .latitude("35.1472227575419")
                .placeUrl("http://place.map.kakao")
                .distance("")
                .build();
        apiShops.add(kakaoKeywordResponseDto);

        // when
        List<ResponseShopKeyword> response = shopService.findMatchingShops(apiShops, ResponseShopKeyword.class);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(1),
                () -> assertThat(response.get(0).getPlaceName()).isEqualTo(targetShop.getPlaceName())
        );
    }

    @DisplayName("Kakao Maps API 장소 데이터와 일치하는 DB 데이터 조회하기, DB에는 존재하지 않지만 Kakao API에 주소 중복 데이터가 존재할 때")
    @Test
    void t3() {
        // given
        Brand brand = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));

        Shop shop = Shop.builder()
                .brand(brand)
                .placeName("인생네컷 청주 충북대점")
                .address("충북 청주시 서원구 1순환로704번길 78")
                .favoriteCnt(0)
                .reviewCnt(0)
                .starRatingAvg(0.0)
                .build();
        shopRepository.save(shop);

        List<KakaoMapSearchDto> apiShops = new ArrayList<>();
        KakaoMapSearchDto kakaoKeywordResponseDto1 = KakaoMapSearchDto.builder()
                .placeName("인생네컷 청주충북대중문점")
                .addressName("충북 청주시 서원구 사창동 413-1")
                .roadAddressName("충북 청주시 서원구 1순환로704번길 78")
                .longitude("127.458256927887")
                .latitude("36.6328952742398")
                .placeUrl("http://place.map.kakao")
                .distance("")
                .build();
        apiShops.add(kakaoKeywordResponseDto1);
        KakaoMapSearchDto kakaoKeywordResponseDto2 = KakaoMapSearchDto.builder()
                .placeName("인생네컷 청주충북대점")
                .addressName("충북 청주시 서원구 사창동 413-1")
                .roadAddressName("충북 청주시 서원구 1순환로704번길 78")
                .longitude("127.458256927887")
                .latitude("36.6328952742398")
                .placeUrl("http://place.map.kakao")
                .distance("")
                .build();
        apiShops.add(kakaoKeywordResponseDto2);

        // when
        List<ResponseShopKeyword> response = shopService.findMatchingShops(apiShops, ResponseShopKeyword.class);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(1),
                () -> assertThat(response.get(0).getPlaceName()).isEqualTo(shop.getPlaceName())
        );
    }
}