package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.assertj.core.api.InstanceOfAssertFactories.stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;


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