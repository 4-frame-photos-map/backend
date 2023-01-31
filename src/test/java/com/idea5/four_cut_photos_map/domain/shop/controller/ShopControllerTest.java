package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
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

    private final String[] brands = {"인생네컷", "하루필름", "포토이즘", "포토그레이", "포토시그니처", "비룸", "포토드링크", "포토매틱", "셀픽스"};


    @DisplayName("TEXT 검색")
    @Test
    void searchByText() throws Exception {
        // given
        String keyword = "포토이즘박스 성수점";

        shopRepository.save(new Shop("인생네컷", "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48", 127.043851506853, 37.5461761379704));
        shopRepository.save(new Shop("포토이즘박스", "포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2", 127.04073790685483, 37.547177362006806));
        shopRepository.save(new Shop("인생네컷", "인생네컷 카페성수로드점", "서울 성동구 서울숲4길 13", 127.042449120263, 37.5475677927281));
        shopRepository.save(new Shop("하루필름", "하루필름 서울숲점", "서울 성동구 서울숲2길 45", 127.043600450617, 37.5464465306291));
        shopRepository.save(new Shop("인생네컷", "인생네컷 서울숲점", "서울 성동구 서울숲4길 20", 127.043010183447, 37.547189170196));
        shopRepository.save(new Shop("픽닷", "픽닷", "서울 성동구 서울숲4길 23-1", 127.043634812377, 37.5471565050697));

        // when
        ResultActions resultActions = mockMvc.perform(get("/shop/search")
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions
                .andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$..address").value("서울 성동구 서울숲2길 17-2"))
                .andExpect(jsonPath("$.result.length()").value(1)) // todo : apiResponse로 감싸면 jsonPath 수정해야 됨
                .andDo(print());

    }

    // todo : 브랜드 검색 api TDD
    @DisplayName("브랜드 검색")
    @Test
    void searchByBrand() {

        /**
         * 참고)
         *   String expectByBrand = "$.[?(@.brand == '%s')]";
         *  .andExpect(jsonPath(expectByBrand, keyword).exists()) // jsonPath에 해당 키워드인 브랜드가 존재하는지
         *  .andExpect(jsonPath(expectByBrand,keywords).value(equalTo(keyword))) // jsonPath에 해당 키워드인 브랜드가 존재하는지
         *  .andExpect(jsonPath("$[0].brand").value(equalTo(keyword)))
         */
        // given


        // when

        // then
    }
    @Test
    @DisplayName("상점 상세보기")
    void 상점_상세보기() throws Exception{

        // given
        Shop shop = shopRepository.save(new Shop("인생네컷", "인생네컷 홍대점", "서울 ~", 12.123, 12.12345));
        String distance = "3km";

        // when
        ResultActions resultActions = mockMvc.perform(get("/shop/detail/%s".formatted(shop.getId()))
                .param("distance", distance)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").value(shop.getId()))
                .andExpect(jsonPath("name").value(shop.getName()))
                .andExpect(jsonPath("address").value(shop.getAddress()))
                .andExpect(jsonPath("latitude").value(shop.getLatitude()))
                .andExpect(jsonPath("longitude").value(shop.getLongitude()))
                .andExpect(jsonPath("distance").value(distance))
                .andDo(print());

    }
}