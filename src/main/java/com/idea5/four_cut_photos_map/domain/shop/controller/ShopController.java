package com.idea5.four_cut_photos_map.domain.shop.controller;

import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseMarker;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import com.idea5.four_cut_photos_map.global.common.data.TempKaKaO;
import jdk.dynalink.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ShopDto> apiShops = TempKaKaO.tempDataBySearch(keyword);
        // db 비교
        List<ResponseShop> shops = shopService.findShops(apiShops, keyword);

        return ResponseEntity.ok(shops);
    }

    //현재 위치 기준, 반경 2km
    @GetMapping("/search/marker")
    public ResponseEntity<Map<String, List<ResponseMarker>>> currentLocationSearch(@ModelAttribute @Valid RequestShop requestShop){

        String[] names = Brand.Names; // 브랜드명 ( 하루필름, 인생네컷 ... )

        // 카카오 api -> 필요한 변수 = {브랜드명, 위도, 경도, 반경}
        // 일단 샘플로 테스트
        Map<String, List<ShopDto>> maps = new HashMap<>();
        for (String brandName : names) {
            List<ShopDto> list = TempKaKaO.tempDataBySearch(brandName);
            maps.put(brandName, list);
        }

        Map<String, List<ResponseMarker>> maker = shopService.findMaker(maps);


        return ResponseEntity.ok(maker);
    }

    @GetMapping("/detail/{shopId}")
    public ResponseEntity<ResponseShopDetail> detail(@PathVariable(name = "shopId") Long id,
                                                     @RequestParam("distance") @NotEmpty(message = "필수 입력 값 입니다.") String distance) {

        ResponseShopDetail shopDetailDto = shopService.findShopById(id, distance);
        return ResponseEntity.ok(shopDetailDto);

    }



}
