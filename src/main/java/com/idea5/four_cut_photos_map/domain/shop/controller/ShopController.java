package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping("/shops")
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShopController {
    private final ShopService shopService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;


    /**
     * 키워드 조회, 정확도순 정렬
     */
    @GetMapping(value = "")
    public ResponseEntity<List<ResponseShopKeyword>> showSearchResultsByKeyword (@RequestParam @NotBlank String keyword,
                                                                                 @RequestParam @NotNull Double userLat,
                                                                                 @RequestParam @NotNull Double userLng,
                                                                                 @AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseShopKeyword> resultShops = new ArrayList<>();

        List<KakaoMapSearchDto> apiShop = shopService.searchKakaoMapByKeyword(keyword, userLat, userLng);
        if(apiShop.isEmpty()) {
            return ResponseEntity.ok(resultShops);
        }

        resultShops = shopService.findMatchingShops(apiShop, ResponseShopKeyword.class);
        if(resultShops.isEmpty()) {
            return ResponseEntity.ok(resultShops);
        }

        if (memberContext != null) {
            resultShops.forEach(resultShop -> {
                        Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                        resultShop.setFavorite(favorite != null);
                    }
            );
        }

        return ResponseEntity.ok(resultShops);
    }

    /**
     * 브랜드별 조회, 거리순 정렬
     */
    @GetMapping("/brand")
    public ResponseEntity<Map<String, Object>> showSearchResultsByBrand (@RequestParam(required = false, defaultValue = "") String brand,
                                                                         @RequestParam(required = false, defaultValue = "2000") Integer radius,
                                                                         @RequestParam @NotNull Double userLat,
                                                                         @RequestParam @NotNull Double userLng,
                                                                         @RequestParam @NotNull Double mapLat,
                                                                         @RequestParam @NotNull Double mapLng,
                                                                         @AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseShopBrand> resultShops = new ArrayList<>();
        String mapCenterAddress = shopService.convertMapCenterCoordToAddress(mapLat, mapLng);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("address", mapCenterAddress);
        responseMap.put("shops", resultShops);

        List<KakaoMapSearchDto> apiShop = shopService.searchKakaoMapByBrand(brand, radius, userLat, userLng, mapLat, mapLng);
        if(apiShop.isEmpty()) {
            return ResponseEntity.ok(responseMap);
        }

        resultShops = shopService.findMatchingShops(apiShop, ResponseShopBrand.class);
        if(resultShops.isEmpty()) {
            return ResponseEntity.ok(responseMap);
        }

        if (memberContext != null) {
            resultShops.forEach(resultShop -> {
                        Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                        resultShop.setFavorite(favorite != null);
                    }
            );
        }

        responseMap.put("shops", resultShops);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 상세 조회
     */
    @GetMapping("/{shop-id}")
    public ResponseEntity<ResponseShopDetail> showDetail (@PathVariable(name = "shop-id") Long id,
                                                          @RequestParam @NotNull Double userLat,
                                                          @RequestParam @NotNull Double userLng,
                                                          @AuthenticationPrincipal MemberContext memberContext) {

        Shop dbShop = shopService.findById(id);
        ResponseShopDetail shopDetailDto = shopService.setResponseDto(dbShop, userLat, userLng);

        List<ResponseShopReviewDto> recentReviews = reviewService.getTop3ShopReviews(shopDetailDto.getId());
        shopDetailDto.setRecentReviews(recentReviews);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(shopDetailDto.getId(), memberContext.getId());
            shopDetailDto.setFavorite(favorite != null);
        }

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        if (shopTitleLogService.existShopTitles(id)) {
//            List<String> shopTitles = shopTitleLogService.getShopTitles(id);
//            shopDetailDto.setShopTitles(shopTitles);
//        }

        return ResponseEntity.ok(shopDetailDto);
    }

    /**
     * 간단 조회, Map Marker 모달용
     */
    @GetMapping("/{shop-id}/info")
    public ResponseEntity<ResponseShopBriefInfo> showBriefInfo (@PathVariable(name = "shop-id") Long id,
                                                                @RequestParam @NotBlank String placeName,
                                                                @RequestParam @NotBlank String distance,
                                                                @AuthenticationPrincipal MemberContext memberContext) {

        ResponseShopBriefInfo responseShopBriefInfo = shopService.setResponseDto(id, placeName, distance);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(responseShopBriefInfo.getId(), memberContext.getId());
            responseShopBriefInfo.setFavorite(favorite != null);
        }

        return ResponseEntity.ok(responseShopBriefInfo);
    }

    // 브랜드별 Map Marker
    // 현재 위치 기준, 반경 2km
//    @GetMapping("/marker")
//    public ResponseEntity<RsData<Map<String, List<ResponseShopMarker>>>> currentLocationSearch(@ModelAttribute @Valid RequestShop requestShop) {
//
//        String[] names = Brand.Names; // 브랜드명 ( 하루필름, 인생네컷 ... )
//
//        Map<String, List<ResponseShopMarker>> maps = new HashMap<>();
//        for (String brandName : names) {
//            List<ResponseShopMarker> list = shopService.searchMarkers(requestShop, brandName);
//            maps.put(brandName, list);
//        }
//
//        return ResponseEntity.ok(
//                new RsData<Map<String, List<ResponseShopMarker>>>(true, "반경 2km 이내 Shop 조회 성공", maps)
//        );
}