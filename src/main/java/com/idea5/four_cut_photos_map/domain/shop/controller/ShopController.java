package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.data.TempApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;

    /**
     * todo : 카카오맵 api 완료시, 표시할 shop 조회
     * 순서)
     * 1. 맵 api로부터, 현재 위치 기준으로 "브랜드별" 조회 및 응답값 받아오기
     * 2. DB와 응답값을 비교
     * 3. 클라이언트에게 응답.
     */

    /**
     * 키워드 검색 (리스트 조회)
     * ex)
     * 하루필름, 인생네컷, 포토이즘, 포토그레이
     */

    @GetMapping("/search")
    public ResponseEntity<List<ResponseShop>> keywordSearch(@RequestParam String keyword){

        // 카카오맵 api 사용 (카카오맵에서 받아온다 가정)
        List<ResponseShop> apiShops = TempApi.tempDataBySearch(keyword);

        // db 비교
        List<ResponseShop> shops = shopService.findShops(apiShops, keyword);
        for (ResponseShop shop : shops) {
            System.out.println("shop = " + shop);
        }

        return null;
    }
    //현재 위치 기준
//    @GetMapping("/search")
//    public ResponseEntity<List<ResponseShop>> currentLocationSearch(@ModelAttribute RequestShop requestShop){
//
//
//
//        // 카카오 맵 API 호출 (임시)
////        List<ResponseShop> apiShops = sampleData();
////        shopService.findShops(apiShops, requestShop);
//        return null;
//    }



}
