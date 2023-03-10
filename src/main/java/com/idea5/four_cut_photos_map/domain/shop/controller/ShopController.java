package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
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

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.BRAND_NOT_FOUND;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DISTANCE_IS_EMPTY;


@RequestMapping("/shops")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;
    private final FavoriteService favoriteService;

    private final ShopTitleLogService shopTitleLogService;


    @GetMapping(value = "")
    public ResponseEntity<RsData<List<ResponseShop>>> showKeywordSearchList(@RequestParam(defaultValue = "????????????") String keyword) throws JsonProcessingException {
        // 1. ???????????? api ?????? ????????? ????????????
        List<KakaoKeywordResponseDto> apiShopJson = shopService.searchByKeyword(keyword);

        // 2. db ???????????? ??????
        List<ResponseShop> shops = shopService.findShops(apiShopJson);

        return ResponseEntity.ok(
                new RsData<List<ResponseShop>>(true, "???????????? Shop ?????? ??????", shops)
        );
    }

    @GetMapping("/brand")
    public ResponseEntity<RsData<List<ResponseShopBrand>>> showBrandListBySearch(@ModelAttribute @Valid RequestBrandSearch requestBrandSearch) {
        // api ?????????, DB?????? ?????? ????????? ??????????????? ??? ?????????
        List<ShopDto> shopDtos = shopService.findByBrand(requestBrandSearch.getBrand());
        if (shopDtos.isEmpty())
            throw new BusinessException(BRAND_NOT_FOUND);


        List<KakaoResponseDto> kakaoApiResponse = shopService.searchBrand(requestBrandSearch);

        List<ResponseShopBrand> resultShops = new ArrayList<>(); // ????????? ?????????

        // ????????? ??? api??? ?????? ????????? Shop ???????????? db??? ????????? Shop ??????
        for (KakaoResponseDto apiShop : kakaoApiResponse) {
            for (ShopDto shopDto : shopDtos) {
                if (apiShop.getPlaceName().equals(shopDto.getPlaceName())) {
                    resultShops.add(ResponseShopBrand.of(apiShop));
                }
            }
        }


        // ?????? ??????, ????????? ????????? ???????????? ?????? ???
        if (resultShops.isEmpty()) {
            return ResponseEntity.ok(new RsData<>(
                    true, String.format("????????? %s???(???) ????????????.", requestBrandSearch.getBrand()), resultShops
            ));
        }

        return ResponseEntity.ok(new RsData<>(
                true, "brand ?????? ??????", resultShops
        ));
    }


    //?????? ?????? ??????, ?????? 2km
    @GetMapping("/marker")
    public ResponseEntity<RsData<Map<String, List<ResponseShopMarker>>>> currentLocationSearch(@ModelAttribute @Valid RequestShop requestShop) {

        String[] names = Brand.Names; // ???????????? ( ????????????, ???????????? ... )

        Map<String, List<ResponseShopMarker>> maps = new HashMap<>();
        for (String brandName : names) {
            List<ResponseShopMarker> list = shopService.searchMarkers(requestShop, brandName);
            maps.put(brandName, list);
        }

        return ResponseEntity.ok(
                new RsData<Map<String, List<ResponseShopMarker>>>(true, "Shop ?????? ??????", maps)
        );
    }

    // todo : @Validated ????????? ?????? ???, httpstatus code ???????????? ??????
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

        return ResponseEntity.ok(shopDetailDto);
    }
}