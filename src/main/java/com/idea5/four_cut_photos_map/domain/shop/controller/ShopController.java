package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.kakao.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopKeyword;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBrand;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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


    @GetMapping(value = "")
    public ResponseEntity<RsData<List<ResponseShopKeyword>>> showSearchesByKeyword(@ModelAttribute @Valid RequestKeywordSearch requestKeywordSearch,
                                                                                   @AuthenticationPrincipal MemberContext memberContext) {
        // todo: 키워드 유효성 검사(유도한 키워드가 맞는지)

        List<KakaoMapSearchDto> apiShop = shopService.searchByKeyword(requestKeywordSearch);
        if(apiShop.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("키워드(%s)에 해당하는 지점이 존재하지 않습니다.", requestKeywordSearch.getKeyword()))
            );

        List<ResponseShopKeyword> resultShops = shopService.compareWithDbShops(apiShop);
        if(resultShops.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("키워드(%s)에 해당하는 지점이 존재하지 않습니다.", requestKeywordSearch.getKeyword()))
            );

        if (memberContext != null) {
            resultShops.stream().forEach(resultShop -> {
                Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                resultShop.setCanBeAddedToFavorites(favorite == null);
                    }
            );
        }

        return ResponseEntity.ok(
                new RsData<>(true, "키워드로 지점 조회 성공, 정확도순 정렬", resultShops)
        );
    }

    @GetMapping("/brand")
    public ResponseEntity<RsData<List<ResponseShopBrand>>> showSearchesByBrand(@ModelAttribute @Valid RequestBrandSearch requestBrandSearch,
                                                                               @AuthenticationPrincipal MemberContext memberContext) {
        String brandForMsg = "전체";
        if(!ObjectUtils.isEmpty(requestBrandSearch.getBrand())) {
            if (!shopService.isRepresentativeBrand(requestBrandSearch.getBrand()))
                throw new BusinessException(INVALID_BRAND);
            else brandForMsg = requestBrandSearch.getBrand();
        }

        List<KakaoMapSearchDto> apiShop = shopService.searchByBrand(requestBrandSearch);
        if(apiShop.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("반경 2km 이내에 %s 지점이 존재하지 않습니다.", brandForMsg))
            );

        List<ResponseShopBrand> resultShops = new ArrayList<>();
        resultShops = shopService.compareWithDbShops(apiShop, resultShops);
        if(resultShops.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("반경 2km 이내에 %s 지점이 존재하지 않습니다.", brandForMsg))
            );

        if (memberContext != null) {
            resultShops.stream().forEach(resultShop -> {
                        Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                        resultShop.setCanBeAddedToFavorites(favorite == null);
                    }
            );
        }

        return ResponseEntity.ok(
                new RsData<>(true, "반경 2km 이내 지점 조회 성공, 거리순 정렬", resultShops)
        );
    }

    // todo : @Validated 유효성 검사 시, httpstatus code 전달하는 방법
    @GetMapping("/{shopId}")
    public ResponseEntity<RsData<ResponseShopDetail>> detail(@PathVariable(name = "shopId") Long id,
                                                             @RequestParam(name = "distance", required = false, defaultValue = "") String distance,
                                                             @AuthenticationPrincipal MemberContext memberContext) {

        if (distance.isEmpty()) throw new BusinessException(DISTANCE_IS_EMPTY);

        Shop dbShop = shopService.findById(id);
        ResponseShopDetail shopDetailDto = shopService.renameShopAndGetPlaceUrl(dbShop, distance);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(shopDetailDto.getId(), memberContext.getId());
            shopDetailDto.setCanBeAddedToFavorites(favorite == null);
        }

        if (shopTitleLogService.existShopTitles(id)) {
            List<String> shopTitles = shopTitleLogService.getShopTitles(id);
            shopDetailDto.setShopTitles(shopTitles);
        }
        return ResponseEntity.ok(
                new RsData<>(true, "지점 상세 조회 성공", shopDetailDto)
        );
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