package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.like.entity.Like;
import com.idea5.four_cut_photos_map.domain.like.repository.LikeRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.common.data.TempKaKaO;
import com.idea5.four_cut_photos_map.member.entity.Member;
import com.idea5.four_cut_photos_map.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    private LikeRepository likeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("상점 상세보기")
    @Test
    void getShopDetail() {
        // given
        Shop shop = Shop.builder()
                .id(1L)
                .brand("인생네컷")
                .name("인생네컷 강남점")
                .address("서울 강남구~")
                .latitude(100.1)
                .longitude(100.2)
                .build();
        shopRepository.save(shop);

        // when
        ResponseShopDetail shopDetail = shopService.findShopById(1L, "3km");

        // then
        assertAll(
                ()->assertThat(shopDetail.getName()).isEqualTo("인생네컷 강남점"),
                ()->assertThat(shopDetail.getAddress()).isEqualTo("서울 강남구~"),
                ()->assertThat(shopDetail.getLatitude()).isEqualTo(100.1),
                ()->assertThat(shopDetail.getLongitude()).isEqualTo(100.2)
        );
    }



}