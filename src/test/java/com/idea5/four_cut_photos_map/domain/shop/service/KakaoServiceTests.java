//package com.idea5.four_cut_photos_map.domain.shop.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
//import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
//import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
//import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
////@SpringBootTest
////@ActiveProfiles("test")
//public class KakaoServiceTests {
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private ShopRepository shopRepository;
//
//    @Test
//    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 지역명 + 즉석사진")
//    void t1() throws JsonProcessingException {
//        // given
//        String keyword = "마포 즉석사진"; // "마포 셀프사진", "마포 포토부스"도 ok
//
//        // when
//        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(keyword);
//
//        // then
//        assertThat(apiShopJson).isNotNull();
//        assertThat(apiShopJson).isNotEmpty();
//        assertThat(apiShopJson.size()).isEqualTo(15);
//        assertThat(apiShopJson.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점");
//        assertThat(apiShopJson.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21");
//        assertThat(apiShopJson.get(0).getLongitude()).isEqualTo("126.922894949096");
//        assertThat(apiShopJson.get(0).getLatitude()).isEqualTo("37.555493447252");
//    }
//
//    @Test
//    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 브랜드 + 지점명")
//    void t2() throws JsonProcessingException {
//        // given
//        String keyword = "인생네컷 홍대동교점";
//
//        // when
//        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(keyword);
//
//        // then
//        assertThat(apiShopJson).isNotNull();
//        assertThat(apiShopJson).isNotEmpty();
//        assertThat(apiShopJson.size()).isEqualTo(1);
//        assertThat(apiShopJson.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점");
//        assertThat(apiShopJson.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21");
//        assertThat(apiShopJson.get(0).getLongitude()).isEqualTo("126.922894949096");
//        assertThat(apiShopJson.get(0).getLatitude()).isEqualTo("37.555493447252");
//    }
//
//    @Test
//    @DisplayName("카카오맵 API 응답 데이터와 DB 데이터 비교하기")
//    void t3() throws JsonProcessingException {
//        // given
//        shopRepository.save(new Shop("인생네컷", "인생네컷 홍대동교점", "서울 마포구 홍익로6길 21"));
//        shopRepository.save(new Shop("하루필름", "하루필름 연남점", "서울 마포구 동교로46길 40"));
//        shopRepository.save(new Shop("포토이즘박스", "포토이즘박스 망원점", "서울 마포구 포은로 88"));
//
//        List<KakaoKeywordResponseDto> apiShopJson = new ArrayList<>();
//
//        KakaoKeywordResponseDto kakaoKeywordResponseDto = KakaoKeywordResponseDto.builder()
//                .placeName("인생네컷 홍대동교점")
//                .roadAddressName("서울 마포구 홍익로6길 21")
//                .longitude("126.922894949096")
//                .latitude("37.555493447252")
//                .build();
//
//        apiShopJson.add(kakaoKeywordResponseDto);
//
//        // when
//        List<ResponseShop> shops = shopService.findShops(apiShopJson);
//
//        // then
//        assertThat(shops).isNotNull();
//        assertThat(shops).isNotEmpty();
//        assertThat(shops.size()).isEqualTo(1);
//        assertThat(shops.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점");
//        assertThat(shops.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21");
//        assertThat(shops.get(0).getLongitude()).isEqualTo(126.922894949096); // Double
//        assertThat(shops.get(0).getLatitude()).isEqualTo(37.555493447252); // Double
//    }
//}
