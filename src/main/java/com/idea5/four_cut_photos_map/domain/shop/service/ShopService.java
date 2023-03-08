package com.idea5.four_cut_photos_map.domain.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KeywordSearchKakaoApi;
import com.idea5.four_cut_photos_map.domain.shoptitle.service.ShopTitleService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ShopRepository shopRepository;
    private final KeywordSearchKakaoApi keywordSearchKakaoApi;

    private final ShopTitleLogService shopTitleLogService;

    public List<ShopDto> findByBrand(String brandName){
        List<Shop> shops = shopRepository.findByBrand(brandName).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
        List<ShopDto> shopDtos = new ArrayList<>();
        for (Shop shop : shops)
            shopDtos.add(ShopDto.of(shop));
        return shopDtos;

    }

    public List<ResponseShop> findShops(List<KakaoKeywordResponseDto> apiShops) {
        List<ResponseShop> responseShops = new ArrayList<>();

        // 카카오 맵 API로 부터 받아온 데이터와 일치하는 DB Shop 가져오기
        for (KakaoKeywordResponseDto apiShop: apiShops) {
            // DB에서 도로명주소로 Shop 조회(비교)
            Shop dbShop = shopRepository.findByRoadAddressName(apiShop.getRoadAddressName()).orElse(null);

            if(dbShop != null) {
                // dbShop, apiSop -> responseShop 변환
                // 위도, 경도는 카카오맵 API로부터, 나머지는 DB Shop으로부터
                ResponseShop responseShop = ResponseShop.from(dbShop, apiShop);

                responseShops.add(responseShop);
            }
        }

        if(responseShops.isEmpty()) {throw new BusinessException(SHOP_NOT_FOUND);}

        return responseShops;
    }


    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
        ResponseShopDetail shopDto = ResponseShopDetail.of(shop, distance);
        return shopDto;

    }

    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public List<KakaoKeywordResponseDto> searchByKeyword(String keyword) throws JsonProcessingException {
        return keywordSearchKakaoApi.searchByKeyword(keyword);
    }

    public List<ResponseShopMarker> searchMarkers(RequestShop shop, String brandName) {
        List<KakaoResponseDto> kakaoShops = keywordSearchKakaoApi.searchMarkers(shop, brandName);
        List<ShopDto> dbShops = findByBrand(brandName);
        List<ResponseShopMarker> resultShops = new ArrayList<>();

        for (KakaoResponseDto kakaoShop : kakaoShops) {
            for (ShopDto dbShop : dbShops) {
                if (kakaoShop.getPlaceName().equals(dbShop.getPlaceName())) {
                    ResponseShopMarker responseShopMarker = ResponseShopMarker.of(kakaoShop);
                    responseShopMarker.setId(dbShop.getId());
                    // 상점이 칭호를 보유했으면 추가
                    if(shopTitleLogService.existShopTitles(dbShop.getId())){
                        responseShopMarker.setShopTitles(shopTitleLogService.getShopTitles(dbShop.getId()));
                    }
                    resultShops.add(responseShopMarker);
                }
            }
        }
        return resultShops;
    }

    public List<KakaoResponseDto> searchBrand(RequestBrandSearch brandSearch) {
        List<KakaoResponseDto> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            list.addAll(keywordSearchKakaoApi.searchByBrand(brandSearch, i));
        }
        return list;
    }
}
