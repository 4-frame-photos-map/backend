package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShopBriefInfo;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBriefInfo;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.INVALID_BRAND;


@RequestMapping("/shops")
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShopController {
    private final ShopService shopService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;


    @GetMapping(value = "")
    public ResponseEntity<RsData<List<ResponseShop>>> showSearchResultsByKeyword (@ModelAttribute @Valid RequestKeywordSearch requestKeywordSearch,
                                                                            @AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseShop> resultShops = new ArrayList<>();

        List<KakaoMapSearchDto> apiShop = shopService.searchKakaoMapByKeyword(requestKeywordSearch);
        if(apiShop.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("키워드(%s)에 해당하는 지점이 존재하지 않습니다.", requestKeywordSearch.getKeyword()),
                            resultShops)
            );

        resultShops = shopService.compareWithDbShops(apiShop);
        if(resultShops.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("키워드(%s)에 해당하는 지점이 존재하지 않습니다.", requestKeywordSearch.getKeyword()),
                            resultShops)
            );

        if (memberContext != null) {
            resultShops.forEach(resultShop -> {
                Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                resultShop.setFavorite(favorite == null);
                    }
            );
        }

        return ResponseEntity.ok(
                new RsData<>(true, "키워드로 지점 조회 성공, 정확도순 정렬", resultShops)
        );
    }

    @GetMapping("/brand")
    public ResponseEntity<RsData<List<ResponseShop>>> showSearchResultsByBrand (@ModelAttribute @Valid RequestBrandSearch requestBrandSearch,
                                                                               @AuthenticationPrincipal MemberContext memberContext) {
        String brandForMsg = "전체";
        List<ResponseShop> resultShops = new ArrayList<>();

        if(!ObjectUtils.isEmpty(requestBrandSearch.getBrand())) {
            if (!shopService.isRepresentativeBrand(requestBrandSearch.getBrand())) throw new BusinessException(INVALID_BRAND);
            else brandForMsg = requestBrandSearch.getBrand();
        }


        List<KakaoMapSearchDto> apiShop = shopService.searchKakaoMapByBrand(requestBrandSearch);
        if(apiShop.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("반경 2km 이내에 %s 지점이 존재하지 않습니다.", brandForMsg),
                            resultShops)
            );

        resultShops = shopService.compareWithDbShops(apiShop);
        if(resultShops.isEmpty())
            return ResponseEntity.ok(
                    new RsData<>(true,
                            String.format("반경 2km 이내에 %s 지점이 존재하지 않습니다.", brandForMsg),
                            resultShops)
            );

        if (memberContext != null) {
            resultShops.forEach(resultShop -> {
                        Favorite favorite = favoriteService.findByShopIdAndMemberId(resultShop.getId(), memberContext.getId());
                        resultShop.setFavorite(favorite == null);
                    }
            );
        }

        return ResponseEntity.ok(
                new RsData<>(true, "반경 2km 이내 지점 조회 성공, 거리순 정렬", resultShops)
        );
    }

    @GetMapping("/{shop-id}")
    public ResponseEntity<RsData<ResponseShopDetail>> showDetail (@PathVariable(name = "shop-id") Long id,
                                                             @RequestParam @NotBlank String distance,
                                                             @AuthenticationPrincipal MemberContext memberContext) {

        Shop dbShop = shopService.findById(id);
        ResponseShopDetail shopDetailDto = shopService.renameShopAndSetResponseDto(dbShop, distance);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(shopDetailDto.getId(), memberContext.getId());
            shopDetailDto.setFavorite(favorite == null);
        }

        List<ResponseReviewDto> recentReviews = reviewService.getTop3ShopReviews(shopDetailDto.getId());
        shopDetailDto.setRecentReviews(recentReviews);

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(shopDetailDto.getId(), memberContext.getId());
            shopDetailDto.setFavorite(favorite == null);
        }

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        if (shopTitleLogService.existShopTitles(id)) {
//            List<String> shopTitles = shopTitleLogService.getShopTitles(id);
//            shopDetailDto.setShopTitles(shopTitles);
//        }

        return ResponseEntity.ok(
                new RsData<>(true, "지점 상세 조회 성공", shopDetailDto)
        );
    }

    @GetMapping("/{shop-id}/info")
    public ResponseEntity<RsData<ResponseShopBriefInfo>> showBriefInfo (@PathVariable(name = "shop-id") Long id,
                                                                        @ModelAttribute @Valid RequestShopBriefInfo requestShopBriefInfo,
                                                                        @AuthenticationPrincipal MemberContext memberContext) {

        ResponseShopBriefInfo responseShopBriefInfo = shopService.setResponseDto(
                id,
                requestShopBriefInfo.getPlaceName(),
                requestShopBriefInfo.getPlaceUrl(),
                requestShopBriefInfo.getDistance()
        );

        if (memberContext != null) {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(responseShopBriefInfo.getId(), memberContext.getId());
            responseShopBriefInfo.setFavorite(favorite == null);
        }

        return ResponseEntity.ok(
                new RsData<>(true, "지점 간단 조회 성공, Map Marker 모달용", responseShopBriefInfo)
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
//
//        return ResponseEntity.ok(
//                new RsData<Map<String, List<ResponseShopMarker>>>(true, "반경 2km 이내 Shop 조회 성공", maps)
//        );
}