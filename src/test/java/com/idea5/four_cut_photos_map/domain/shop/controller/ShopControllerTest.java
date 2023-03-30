package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Slf4j
class ShopControllerTest {
    // todo : 실패 케이스도 추가하기
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopService shopService;

    // todo : 브랜드 검색 api TDD
    @DisplayName("브랜드 검색")
    @Test
    void searchByBrand() throws Exception {

        /**
         * 참고)
         *   String expectByBrand = "$.[?(@.brand == '%s')]";
         *  .andExpect(jsonPath(expectByBrand, keyword).exists()) // jsonPath에 해당 키워드인 브랜드가 존재하는지
         *  .andExpect(jsonPath(expectByBrand,keywords).value(equalTo(keyword))) // jsonPath에 해당 키워드인 브랜드가 존재하는지
         *  .andExpect(jsonPath("$[0].brand").value(equalTo(keyword)))
         */
        // given

        String searchBrand = "인생네컷";
        double x = 127.134898;
        double y = 36.833922;

        RequestBrandSearch requestBrandSearch = new RequestBrandSearch(searchBrand, x, y);

        shopService.searchKakaoMapByBrand(requestBrandSearch);
        shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48",0,0,0.0));
        shopRepository.save(new Shop("포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2",0,0,0.0));
        shopRepository.save(new Shop("인생네컷 카페성수로드점", "서울 성동구 서울숲4길 13",0,0,0.0));
        shopRepository.save(new Shop("하루필름 서울숲점", "서울 성동구 서울숲2길 45",0,0,0.0));
        shopRepository.save(new Shop("인생네컷 서울숲점", "서울 성동구 서울숲4길 20",0,0,0.0));
        shopRepository.save(new Shop("인생네컷 충남천안두정먹거리공원점", "충남 천안시 서북구 원두정2길 21",0,0,0.0));

        // when
        ResultActions resultActions = mockMvc.perform(get("/shops/brand")
                .param("brand", searchBrand)
                .param("longitude", String.valueOf(x))
                .param("latitude", String.valueOf(y))
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }
    @Test
    @DisplayName("상점 상세보기")
    void 상점_상세보기() throws Exception{

        // given
        Shop shop = shopRepository.save(new Shop("인생네컷 홍대점", "서울 ~",0,0,0.0));
        String distance = "3km";

        // when
        ResultActions resultActions = mockMvc.perform(get("/shops/%s".formatted(shop.getId()))
                .param("distance", distance)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").value(shop.getId()))
                .andExpect(jsonPath("name").value(shop.getPlaceName()))
                .andExpect(jsonPath("address").value(shop.getRoadAddressName()))
                .andExpect(jsonPath("distance").value(distance))
                .andDo(print());

    }



    @DisplayName("2km 이내 마커 표시")
    @Test
    void searchMarker() throws Exception {
        // given
        double x = 127.134898;
        double y = 36.833922;
        Point point = new Point(127.134898, 36.833922); // 두정동 위치


        shopRepository.save(new Shop("인생네컷 충남천안두정먹거리공원점", "충남 천안시 서북구 원두정2길 21",0,0,0.0));
        shopRepository.save(new Shop("포토이즘박스 두정점", "충남 천안시 서북구 원두정2길 21",0,0,0.0));
        shopRepository.save(new Shop( "하루필름 천안점", "충남 천안시 동남구 신부동 459-1",0,0,0.0));


        // when
        ResultActions resultActions = mockMvc.perform(get("/shops/marker")
                .param("longitude", String.valueOf(point.getX()))
                .param("latitude", String.valueOf(point.getY()))
                .contentType(MediaType.APPLICATION_JSON));



        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.result.인생네컷.length()").value(1)) // todo : apiResponse로 감싸면 jsonPath 수정해야 됨
                .andExpect(jsonPath("$.result.포토이즘박스.length()").value(1)) // todo : apiResponse로 감싸면 jsonPath 수정해야 됨
                .andExpect(jsonPath("$.result.하루필름.length()").value(0)) // todo : apiResponse로 감싸면 jsonPath 수정해야 됨
                .andDo(print());
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기, DB에 동일 데이터 존재")
    @Test
    void showListSearchedByKeyword() throws Exception {
        // Given
        String keyword = "마포";
        double cur_x = 126.76819064893;
        double cur_y = 37.662161386065;
        shopRepository.save(new Shop("인생네컷 홍대동교점", "서울 마포구 홍익로6길 21",0,0,0.0));
        shopRepository.save(new Shop("하루필름 연남점", "서울 마포구 동교로46길 40",0,0,0.0));

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("longitude", String.valueOf(cur_x))
                        .param("latitude", String.valueOf(cur_y))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                // Then
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("showListSearchedByKeyword"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("키워드로 Shop 조회 성공"))
                .andExpect(jsonPath("$.result.*", hasSize(2)))

                .andExpect(jsonPath("$.result[0].placeName", containsString("인생네컷 홍대동교점")))
                .andExpect(jsonPath("$.result[0].roadAddressName", containsString("서울 마포구 홍익로6길 21")))
                .andExpect(jsonPath("$.result[0].longitude", equalTo(126.922894949096)))
                .andExpect(jsonPath("$.result[0].latitude", equalTo(37.555493447252)))
                .andExpect(jsonPath("$.result[0].distance", equalTo("18.1km")))


                .andExpect(jsonPath("$.result[1].placeName", containsString("하루필름 연남점")))
                .andExpect(jsonPath("$.result[1].roadAddressName", containsString("서울 마포구 동교로46길 40")))
                .andExpect(jsonPath("$.result[1].longitude", equalTo(126.926725005048)))
                .andExpect(jsonPath("$.result[1].latitude", equalTo(37.5621542536479)))
                .andExpect(jsonPath("$.result[1].distance", equalTo("17.9km")));
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기, DB에 동일 데이터 존재하지 않음")
    @Test
    void showListSearchedByKeywordWithNoResults() throws Exception {
        // Given
        String keyword = "마포";
        double cur_x = 126.76819064893;
        double cur_y = 37.662161386065;

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("longitude", String.valueOf(cur_x))
                        .param("latitude", String.valueOf(cur_y))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                // Then
                //.andDo(print())
                .andExpect(handler().methodName("showListSearchedByKeyword"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorMessage", containsString("상점을 찾을 수 없습니다.")));
    }

    static class Point{
        private double x,y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}