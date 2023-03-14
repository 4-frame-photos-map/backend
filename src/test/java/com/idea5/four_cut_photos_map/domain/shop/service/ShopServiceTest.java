package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBrand;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopMarker;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ShopServiceTest {

    private final Double X = 127.134898;
    private final Double Y = 36.833922;
    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;


    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("상점 상세보기")
    @Test
    void getShopDetail() {
        // given
        Shop shop = Shop.builder()
                .id(1L)
                .placeName("인생네컷 강남점")
                .roadAddressName("서울 강남구~")
                .favoriteCnt(0)
                .build();
        shopRepository.save(shop);

        // when
        ResponseShopDetail shopDetail = shopService.findShopById(1L, "3km");

        // then
        assertAll(
                ()->assertThat(shopDetail.getName()).isEqualTo("인생네컷 강남점"),
                ()->assertThat(shopDetail.getAddress()).isEqualTo("서울 강남구~")
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

    @DisplayName("Brand 명으로 검색 시, 카카오 맵 api와 DB에 저장되어 있는 placeName을 비교")
    @Test
    void findShopByBrandName() {
//        // given
        String keyword = "인생네컷";
        RequestBrandSearch requestBrandSearch = new RequestBrandSearch("인생네컷", X, Y);
        shopRepository.save(new Shop("인생네컷", "인생네컷 충남천안두정먹거리공원점", "충남 천안시 섭구구 원두정2길 21",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 천안불당로드점", "충남 천안시 서북점 불당33길 26",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 test1점", "test",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 test2점", "test1",0));

        shopRepository.save(new Shop("하루필름", "하루필름 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토이즘", "포토이즘 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토그레이", "포토그레이 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토시그니처", "포토시그니처 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("비룸", "비룸 홍대점", "서울 ~",0));
        shopRepository.save(new Shop("포토드링크", "포토드링크 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토매틱", "포토매틱 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("셀픽스", "셀픽스 홍대점", "서울 강남구~",0));



        // when
        List<KakaoResponseDto> kakaoResponseDtos = shopService.searchBrand(requestBrandSearch);
        List<ShopDto> shopDtos = shopService.findByBrand(keyword);
        List<ResponseShopBrand> resultShops = new ArrayList<>(); // 응답값 리스트

        for (KakaoResponseDto apiShop : kakaoResponseDtos) {
            for (ShopDto shopDto : shopDtos) {
                if (apiShop.getPlaceName().equals(shopDto.getPlaceName())) {
                    resultShops.add(ResponseShopBrand.of(apiShop));
                }
            }
        }

        // then
        assertAll(
                () -> assertThat(resultShops.size()).isEqualTo(3),
                () -> {
                    for (ResponseShopBrand shop : resultShops)
                        assertThat(shop.getPlaceName().contains(keyword)).isTrue();
                }
        );
    }

    @DisplayName("반경 2km 이내, 마커 표시")
    @Test
    void showMarkers() {
        // given
        String[] brandNames = Brand.Names;
        RequestShop requestShop = new RequestShop(X,Y);
        // 2km 이내 데이터(true)
        shopRepository.save(new Shop("인생네컷", "인생네컷 충남천안두정먹거리공원점", "충남 천안시 섭구구 원두정2길 21",0));
        shopRepository.save(new Shop("포토이즘박스", "포토이즘박스 두정점", "충남 천안시 서북구 원두정2길 21",0));
        // 2km 밖 데이터(false)
        shopRepository.save(new Shop("인생네컷", "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 천안불당로드점", "충남 천안시 서북점 불당33길 26",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 test1점", "test",0));
        shopRepository.save(new Shop("인생네컷", "인생네컷 test2점", "test1",0));
        shopRepository.save(new Shop("하루필름", "하루필름 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토이즘", "포토이즘 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토그레이", "포토그레이 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토시그니처", "포토시그니처 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("비룸", "비룸 홍대점", "서울 ~",0));
        shopRepository.save(new Shop("포토드링크", "포토드링크 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("포토매틱", "포토매틱 홍대점", "서울 강남구~",0));
        shopRepository.save(new Shop("셀픽스", "셀픽스 홍대점", "서울 강남구~",0));


        // when
        Map<String, List<ResponseShopMarker>> map = new HashMap<>();
        int totalSize = 0;
        for (String brandName : brandNames) {
            List<ResponseShopMarker> responseShopMarkers = shopService.searchMarkers(requestShop, brandName);
            System.out.println("responseShopMarkers.size() = " + responseShopMarkers.size());
            for (ResponseShopMarker responseShopMarker : responseShopMarkers) {
                System.out.println(responseShopMarker.getPlaceName());
                System.out.println(responseShopMarker.getRoadAddressName());
            }
            map.put(brandName, responseShopMarkers);
            totalSize += responseShopMarkers.size();
        }

        // then
        int finalTotalSize = totalSize;
        assertAll(
                () -> assertThat(finalTotalSize).isEqualTo(2),
                () -> assertThat(map.get("인생네컷").size()).isEqualTo(1),
                () -> assertThat(map.get("포토이즘박스").size()).isEqualTo(1),
                () -> assertThat(map.get("하루필름").size()).isEqualTo(0)
        );
    }

}