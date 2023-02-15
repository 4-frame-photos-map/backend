package com.idea5.four_cut_photos_map.domain.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SearchShopByKeywordTests {
    @Autowired
    private ShopService shopService;

    @Test
    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 지역명 + 즉석사진")
    void t1() throws JsonProcessingException {
        // given
        String keyword = "마포 즉석사진"; // "마포 셀프사진", "마포 포토부스"도 ok

        // when
        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(keyword);

        // then
        assertThat(apiShopJson).isNotNull();
        assertThat(apiShopJson).isNotEmpty();
        assertThat(apiShopJson.size()).isEqualTo(15);
        assertThat(apiShopJson.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점");
        assertThat(apiShopJson.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21");
        assertThat(apiShopJson.get(0).getX()).isEqualTo("126.922894949096");
        assertThat(apiShopJson.get(0).getY()).isEqualTo("37.555493447252");
    }

    @Test
    @DisplayName("카카오맵 API 호출하여 키워드로 장소 검색하기, 키워드 = 브랜드 + 지점명")
    void t2() throws JsonProcessingException {
        // given
        String keyword = "인생네컷 홍대동교점";

        // when
        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(keyword);

        // then
        assertThat(apiShopJson).isNotNull();
        assertThat(apiShopJson).isNotEmpty();
        assertThat(apiShopJson.size()).isEqualTo(1);
        assertThat(apiShopJson.get(0).getPlaceName()).isEqualTo("인생네컷 홍대동교점");
        assertThat(apiShopJson.get(0).getRoadAddressName()).isEqualTo("서울 마포구 홍익로6길 21");
        assertThat(apiShopJson.get(0).getX()).isEqualTo("126.922894949096");
        assertThat(apiShopJson.get(0).getY()).isEqualTo("37.555493447252");
    }
}
