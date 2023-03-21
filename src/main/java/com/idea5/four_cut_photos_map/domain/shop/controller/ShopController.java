package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBrand;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopMarker;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DISTANCE_IS_EMPTY;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.INVALID_BRAND;


@RequestMapping("/shops")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;
    private final FavoriteService favoriteService;

    private final ShopTitleLogService shopTitleLogService;
    private final String NO_CONTENT_MATCHING_KEYWORD = "키워드(%s)에 해당하는 지점이 존재하지 않습니다.";
    private final String NO_CONTENT_WITHIN_RADIUS = "반경 2km 이내에 %s 지점이 존재하지 않습니다.";

    @GetMapping(value = "")
    public ResponseEntity<RsData<List<ResponseShop>>> showListSearchedByKeyword(@ModelAttribute @Valid RequestKeywordSearch requestKeywordSearch) throws JsonProcessingException {
        // todo: 키워드 유효성 검사(유도한 키워드가 맞는지)

        // 1. 카카오맵 api 응답 데이터 받아오기
        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(requestKeywordSearch);
        if(apiShopJson.isEmpty())
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_MATCHING_KEYWORD, requestKeywordSearch.getKeyword())
            ));

        // 2. db 데이터와 비교
        List<ResponseShop> shops = shopService.compareShopsForKeyword(apiShopJson);
        if(shops.isEmpty())
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_MATCHING_KEYWORD, requestKeywordSearch.getKeyword())
            ));

        return ResponseEntity.ok(
                new RsData<>(true, "키워드로 Shop 조회 성공", shops)
        );
    }

    @GetMapping("/brand")
    public ResponseEntity<RsData<List<ResponseShopBrand>>> showBrandListBySearch(@ModelAttribute @Valid RequestBrandSearch requestBrandSearch) {
        // 대표 브랜드에 해당하는지 먼저 확인
        if(shopService.isRepresentativeBrand(requestBrandSearch.getBrand()) == false)
            throw new BusinessException(INVALID_BRAND);

        // api 검색전, DB에서 먼저 있는지 확인하는게 더 효율적
        List<ShopDto> shopDtos = shopService.findByBrand(requestBrandSearch.getBrand());
        if (shopDtos.isEmpty())
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_WITHIN_RADIUS, requestBrandSearch.getBrand())
            ));

        List<KakaoKeywordResponseDto> kakaoApiResponse = shopService.searchBrand(requestBrandSearch);
        List<ResponseShopBrand> resultShops = new ArrayList<>(); // 응답값 리스트

        // 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (KakaoKeywordResponseDto apiShop : kakaoApiResponse) {
            for (ShopDto shopDto : shopDtos) {
                if (apiShop.getRoadAddressName().equals(shopDto.getRoadAddressName())) {
                    resultShops.add(ResponseShopBrand.of(apiShop));
                    break;
                }
            }
        }

        // 검색 결과, 근처에 원하는 브랜드가 없을 때
        if (resultShops.isEmpty()) {
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_WITHIN_RADIUS, requestBrandSearch.getBrand())
            ));
        }

        return ResponseEntity.ok(new RsData<>(
                true, "브랜드로 반경 2km 이내 Shop 조회 성공", resultShops
        ));
    }


    //현재 위치 기준, 반경 2km
    @GetMapping("/marker")
    public ResponseEntity<RsData<List<ResponseShopMarker>>> currentLocationSearch(@ModelAttribute @Valid RequestShop requestShop) {

        // 1. 카카오맵 api 응답 데이터 받아오기
        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByCurrentLocation(requestShop);
        if(apiShopJson.isEmpty())
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_WITHIN_RADIUS, "전체 브랜드")
            ));

        // 2. db 데이터와 비교
        List<ResponseShopMarker> shops = shopService.compareShopsWithinRadius(apiShopJson);
        if(shops.isEmpty())
            return ResponseEntity.ok(new RsData<>(
                    true, String.format(NO_CONTENT_WITHIN_RADIUS, "전체 브랜드")
            ));

        return ResponseEntity.ok(
                new RsData<>(true, "반경 2km 이내 Shop 조회 성공", shops)
        );
    }

    // todo : @Validated 유효성 검사 시, httpstatus code 전달하는 방법
    @GetMapping("/{shopId}")
    public ResponseEntity<ResponseShopDetail> detail(@PathVariable(name = "shopId") Long id,
                                                     @RequestParam(name = "distance", required = false, defaultValue = "") String distance,
                                                     @AuthenticationPrincipal MemberContext memberContext) {
        if (distance.isEmpty()) {
            throw new BusinessException(DISTANCE_IS_EMPTY);
        }
        ResponseShopDetail shopDetailDto = shopService.findShopById(id, distance);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(shopDetailDto.getId(), memberContext.getId());

            if (favorite == null) {
                shopDetailDto.setCanBeAddedToFavorites(true);
            } else {
                shopDetailDto.setCanBeAddedToFavorites(false);
            }
        }

        if (shopTitleLogService.existShopTitles(id)) {
            List<String> shopTitles = shopTitleLogService.getShopTitles(id);
            shopDetailDto.setShopTitles(shopTitles);
        }

        return ResponseEntity.ok(shopDetailDto); // todo: 응답구조에 맞게 처리
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
////
//        return ResponseEntity.ok(
//                new RsData<Map<String, List<ResponseShopMarker>>>(true, "반경 2km 이내 Shop 조회 성공", maps)
//        );
}