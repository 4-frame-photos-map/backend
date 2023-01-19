package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.data.TempKaKaO;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

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
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    private final String[] keywords = {"인생네컷", "하루필름", "포토이즘", "포토그레이", "포토시그니처", "비룸", "포토드링크", "포토매틱", "셀픽스"};


    @DisplayName("키워드 검색")
    @Test
    void 키워드_검색() throws Exception {
        // given

        String expectByBrand = "$.[?(@.brand == '%s')]";
        String keyword = "인생네컷";
        List<ShopDto> apiShops = TempKaKaO.tempDataBySearch(keyword); // 카카오 api에서 받아왔다고 가정. todo : restTeamplate 추가 후, 수정

        shopRepository.save(new Shop("인생네컷", "인생네컷 강남점", "서울 강남구~", 100.1, 100.1));
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
        ResultActions resultActions = mockMvc.perform(get("/shop/search")
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath(expectByBrand, keywords).exists()) // jsonPath에 해당 키워드인 브랜드가 존재하는지
//                .andExpect(jsonPath(expectByBrand,keywords).value(equalTo(keyword))) // jsonPath에 해당 키워드인 브랜드가 존재하는지
//                .andExpect(jsonPath("$[0].brand").value(equalTo(keyword)))
                .andExpect(jsonPath("$.length()").value(3)) // todo : apiResponse로 감싸면 jsonPath 수정해야 됨
                .andDo(print());
    }
    @Test
    @DisplayName("글 상세보기")
    void 글_상세보기() throws Exception{

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