package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopKeyword;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
public class KakaoMapSearchServiceTests {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 지역명")
    void t1() {
        // given
        String keyword = "마포";
        Double userLat = 37.662161386065;
        Double userLng = 126.76819064893;

        // when
        List<KakaoMapSearchDto> apiShopJson = shopService.searchKakaoMapByKeyword(keyword, userLat, userLng);

        // then
        assertAll(
                () -> assertThat(apiShopJson).isNotEmpty(),
                () -> assertThat(apiShopJson.size()).isEqualTo(30),
                () -> assertThat(apiShopJson).extracting(KakaoMapSearchDto::getPlaceName, KakaoMapSearchDto::getAddressName, KakaoMapSearchDto::getRoadAddressName, KakaoMapSearchDto::getDistance)
                        .contains(
                                tuple("인생네컷 홍대동교점","서울 마포구 동교동 162-14","서울 마포구 홍익로6길 21", "18.1km")
                        )
        );

    }

    @Test
    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 지역명 + 브랜드")
    void t2() {
        // given
        String keyword = "성수 포토이즘";
        Double userLat = 37.662161386065;
        Double userLng = 126.76819064893;

        // when
        List<KakaoMapSearchDto> apiShopJson = shopService.searchKakaoMapByKeyword(keyword, userLat, userLng);

        // then
        assertAll(
                () -> assertThat(apiShopJson).isNotEmpty(),
                () -> assertThat(apiShopJson.size()).isEqualTo(10),
                () -> assertThat(apiShopJson).extracting(KakaoMapSearchDto::getPlaceName, KakaoMapSearchDto::getRoadAddressName, KakaoMapSearchDto::getDistance)
                        .contains(
                                tuple("포토이즘박스 성수점","서울 성동구 서울숲2길 17-2","27.2km")
                        )
        );
    }

    @Test
    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 브랜드 + 지점명")
    void t3() {
        // given
        String keyword = "인생네컷 홍대동교점";
        Double userLat = 37.662161386065;
        Double userLng = 126.76819064893;

        // when
        List<KakaoMapSearchDto> apiShopJson = shopService.searchKakaoMapByKeyword(keyword, userLat, userLng);

        // then
        assertAll(
                () -> assertThat(apiShopJson).isNotEmpty(),
                () -> assertThat(apiShopJson.size()).isEqualTo(1),
                () -> assertThat(apiShopJson.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점"),
                () ->  assertThat(apiShopJson.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21"),
                () ->  assertThat(apiShopJson.get(0).getLongitude()).isEqualTo("126.922894949096"),
                () -> assertThat(apiShopJson.get(0).getLatitude()).isEqualTo("37.555493447252"),
                () ->  assertThat(apiShopJson.get(0).getDistance()).isEqualTo("18.1km"));
    }

    @Test
    @DisplayName("카카오맵 API 응답 데이터와 DB 데이터 비교하기")
    void t4() {
        // given
        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));
        Brand brand3 = brandRepository.save(new Brand(MajorBrand.HARUFILM.getBrandName(), MajorBrand.HARUFILM.getFilePath()));

        shopRepository.save(new Shop(brand3, "하루필름 연남점", "서울 마포구 동교로46길 40",0,0,0.0));
        shopRepository.save(new Shop(brand1, "인생네컷 카페성수로드점", "서울 성동구 서울숲4길 13",0,0,0.0));
        shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2",0,0,0.0));

        List<KakaoMapSearchDto> apiShopJson = new ArrayList<>();

        KakaoMapSearchDto kakaoKeywordResponseDto = KakaoMapSearchDto.builder()
                .placeName("하루필름 연남점")
                .roadAddressName("서울 마포구 동교로46길 40")
                .longitude("126.926725005048")
                .latitude("37.5621542536479")
                .distance("17.9km")
                .build();

        apiShopJson.add(kakaoKeywordResponseDto);

        // when
        List<ResponseShopKeyword> shops = shopService.findMatchingShops(apiShopJson, ResponseShopKeyword.class);

        // then
        assertAll(
                () -> assertThat(shops).isNotEmpty(),
                () -> assertThat(shops.size()).isEqualTo(1),
                () -> assertThat(shops.get(0).getPlaceName()).isEqualTo("하루필름 연남점"),
                () ->  assertThat(shops.get(0).getLongitude()).isEqualTo("126.926725005048"),
                () ->   assertThat(shops.get(0).getLatitude()).isEqualTo("37.5621542536479"),
                () ->   assertThat(shops.get(0).getDistance()).isEqualTo("17.9km"));
    }
}