package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Slf4j
class ShopControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void createSampleData(){
        Brand brand1 = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
        Brand brand2 = brandRepository.save(new Brand(MajorBrand.PHOTOISM.getBrandName(), MajorBrand.PHOTOISM.getFilePath()));
        Brand brand3 = brandRepository.save(new Brand(MajorBrand.HARUFILM.getBrandName(), MajorBrand.HARUFILM.getFilePath()));
        Brand brand4 = brandRepository.save(new Brand(MajorBrand.PHOTOGRAY.getBrandName(), MajorBrand.PHOTOGRAY.getFilePath()));
        Brand brand5 = brandRepository.save(new Brand("기타", "https://--"));

        shopRepository.save(new Shop(brand1, "인생네컷 카페성수로드점", "서울 성동구 서울숲4길 13",0,0,0.0));
        shopRepository.save(new Shop(brand1, "인생네컷 서울숲점", "서울 성동구 서울숲4길 20",0,0,0.0));
        shopRepository.save(new Shop(brand2, "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2",0,0,0.0));
        shopRepository.save(new Shop(brand3, "하루필름 서울숲점", "서울 성동구 서울숲2길 45",0,0,0.0));
        shopRepository.save(new Shop(brand1, "인생네컷 서울이태원점", "서울 용산구 이태원로 171",0,0,0.0));
        shopRepository.save(new Shop(brand4, "셀픽스 건대점", null,0,0,0.0));
        shopRepository.save(new Shop(brand5, "포토그레이 서울 성수점", "서울 성동구 성수동2가 310-3",0,0,0.0));
    }

    @AfterEach
    public void clearData(){
        databaseCleaner.execute();
    }

    @DisplayName("지도 중심좌표 반경 내 대표 브랜드 검색")
    @Test
    void t1() throws Exception {
        // Given
        String searchBrand = "인생네컷";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;
        double mapLat = 37.546912668813;
        double mapLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc.perform(get("/shops/brand")
                .param("brand", searchBrand)
                .param("userLat", String.valueOf(userLat))
                .param("userLng", String.valueOf(userLng))
                .param("mapLat", String.valueOf(mapLat))
                .param("mapLng", String.valueOf(mapLng))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByBrand"))
                .andExpect(jsonPath("$.shops", hasSize(2)))

                .andExpect(jsonPath("$.shops[0].id", equalTo(1)))
                .andExpect(jsonPath("$.shops[0].place_name", equalTo("인생네컷 카페성수로드점")))
                .andExpect(jsonPath("$.shops[0].distance", equalTo("136m")))

                .andExpect(jsonPath("$.shops[1].id", equalTo(2)))
                .andExpect(jsonPath("$.shops[1].place_name", equalTo("인생네컷 서울숲점")))
                .andExpect(jsonPath("$.shops[1].distance", equalTo("168m")));
    }

    @DisplayName("지도 중심좌표 반경 내 대표 브랜드 검색, 사용자 현재위치 좌표와 지도 중심좌표가 다른 경우")
    @Test void t2() throws Exception {
        // Given
        String searchBrand = "인생네컷";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // 서울특별시 용산구 이태원로2길 16 (용산동3가)
        double mapLat = 37.534272313844;
        double mapLng = 126.97534763076;

        // when
        ResultActions resultActions = mockMvc.perform(get("/shops/brand")
                .param("brand", searchBrand)
                .param("userLat", String.valueOf(userLat))
                .param("userLng", String.valueOf(userLng))
                .param("mapLat", String.valueOf(mapLat))
                .param("mapLng", String.valueOf(mapLng))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
//                .andDo(print());
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByBrand"))
                .andExpect(jsonPath("$.shops", hasSize(1)))

                .andExpect(jsonPath("$.shops[0].id", equalTo(5)))
                .andExpect(jsonPath("$.shops[0].place_name", equalTo("인생네컷 서울이태원점")))
                .andExpect(jsonPath("$.shops[0].distance", equalTo("4.4km")));
    }

    @DisplayName("지도 중심좌표 반경 내 전체 브랜드 검색")
    @Test void t3() throws Exception {
        // Given
        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;
        double mapLat = 37.546912668813;
        double mapLng = 127.0411420343;

        // when
        ResultActions resultActions = mockMvc.perform(get("/shops/brand")
                .param("userLat", String.valueOf(userLat))
                .param("userLng", String.valueOf(userLng))
                .param("mapLat", String.valueOf(mapLat))
                .param("mapLng", String.valueOf(mapLng))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
//                .andDo(print());
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByBrand"))
                .andExpect(jsonPath("$.shops", hasSize(4)))

                .andExpect(jsonPath("$.shops[0].id", equalTo(3)))
                .andExpect(jsonPath("$.shops[0].place_name", equalTo("포토이즘박스 성수점")))
                .andExpect(jsonPath("$.shops[0].distance", equalTo("46m")))

                .andExpect(jsonPath("$.shops[1].id", equalTo(1)))
                .andExpect(jsonPath("$.shops[1].place_name", equalTo("인생네컷 카페성수로드점")))

                .andExpect(jsonPath("$.shops[2].id", equalTo(2)))
                .andExpect(jsonPath("$.shops[2].place_name", equalTo("인생네컷 서울숲점")))

                .andExpect(jsonPath("$.shops[3].id", equalTo(4)))
                .andExpect(jsonPath("$.shops[3].place_name", equalTo("하루필름 서울숲점")));
    }

    @Test
    @DisplayName("상점 상세조회")
    void t4() throws Exception {

        // Given
        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc.perform(get("/shops/%d".formatted(4))
                .param("userLat", String.valueOf(userLat))
                .param("userLng", String.valueOf(userLng))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions
//                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().methodName("getShopDetail"))
                .andExpect(jsonPath("$.place_name", equalTo("하루필름 서울숲점")))
                .andExpect(jsonPath("$.distance", equalTo("223m")))
                .andExpect(jsonPath("$.latitude", equalTo("37.5464465306291"))) // get Cache from Redis
                .andExpect(jsonPath("$.longitude", equalTo("127.043600450617"))); // get Cache from Redis
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기(정확도순 정렬), DB에 동일 데이터 존재")
    @Test
    void t5() throws Exception {
        // Given
        String keyword = "서울숲";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLng", String.valueOf(userLng))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));

        // Then
        resultActions
//        .andDo(print());
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByKeyword"))
                 .andExpect(jsonPath("$.*", hasSize(4)))

                .andExpect(jsonPath("$.[0].id", equalTo(3)))
                .andExpect(jsonPath("$.[0].place_name", equalTo("포토이즘박스 성수점")))
                .andExpect(jsonPath("$.[0].distance", equalTo("46m")))

                .andExpect(jsonPath("$.[1].id", equalTo(4)))
                .andExpect(jsonPath("$.[1].place_name", equalTo("하루필름 서울숲점")))
                .andExpect(jsonPath("$.[1].distance", equalTo("223m")))

                .andExpect(jsonPath("$.[2].id", equalTo(2)))
                .andExpect(jsonPath("$.[2].place_name", equalTo("인생네컷 서울숲점")))
                .andExpect(jsonPath("$.[2].distance", equalTo("168m")))

                .andExpect(jsonPath("$.[3].id", equalTo(1)))
                .andExpect(jsonPath("$.[3].place_name", equalTo("인생네컷 카페성수로드점")))
                .andExpect(jsonPath("$.[3].distance", equalTo("136m")));
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기, DB에 동일 데이터 존재하지 않음")
    @Test
    void t6() throws Exception {
        // Given
        String keyword = "마포";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLng", String.valueOf(userLng))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));
        // Then
        resultActions
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByKeyword"))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기, 주소값이 null이고 장소명이 일치하는 데이터도 포함")
    @Test
    void t7() throws Exception {
        // Given
        String keyword = "성수 셀픽스";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLng", String.valueOf(userLng))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));
        // Then
        resultActions
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByKeyword"))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", equalTo(6)))
                .andExpect(jsonPath("$.[0].place_name", equalTo("셀픽스 건대점")));
    }

    @DisplayName("키워드로 조회된 상점 리스트 보여주기, Kakao Maps API에서 일반 사진관으로 분류되어있지만 즉석사진 브랜드인 데이터")
    @Test
    void t8() throws Exception {
        // Given
        String keyword = "성수 포토그레이";

        // 서울특별시 성동구 서울숲2길 22-2 (성수동1가)
        double userLat = 37.546912668813;
        double userLng = 127.0411420343;

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/shops")
                        .param("keyword", keyword)
                        .param("userLat", String.valueOf(userLat))
                        .param("userLng", String.valueOf(userLng))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));
        // Then
        resultActions
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("searchShopsByKeyword"))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", equalTo(7)))
                .andExpect(jsonPath("$.[0].place_name", equalTo("포토그레이 서울 성수점")));
    }
}