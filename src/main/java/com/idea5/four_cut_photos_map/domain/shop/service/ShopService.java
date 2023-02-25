package com.idea5.four_cut_photos_map.domain.shop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.RequiredArgsConstructor;
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

    public List<ResponseShop> findShops(List<KaKaoSearchResponseDto.Document> apiShops) {
        List<ResponseShop> responseShops = new ArrayList<>();

        // 카카오 맵 api로 부터 받아온 Shop과 db에 저장된 Shop 비교
        for (KaKaoSearchResponseDto.Document apiShop: apiShops) {
            //log.info("장소명="+apiShop.getPlace_name());

            // db에서 장소명으로 shop 조회
            Shop dbShop = shopRepository.findByPlaceName(apiShop.getPlace_name()).orElse(null);

            // entity -> dto 변환
            if(dbShop != null) {
                ResponseShop responseShop = ResponseShop.from(dbShop);

                // Api Shop과 비교 후 저장
                if (apiShop.getPlace_name().equals(responseShop.getPlaceName())
                        && Double.parseDouble(apiShop.getX()) == responseShop.getLongitude()
                        && Double.parseDouble(apiShop.getY()) == responseShop.getLatitude()) {
                    responseShops.add(responseShop);
                }

            }
        }

        if(responseShops.isEmpty())
            throw new BusinessException(SHOP_NOT_FOUND);

        return responseShops;
    }


    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
        ResponseShopDetail shopDto = ResponseShopDetail.of(shop, distance);
        return shopDto;

    }

    public KaKaoSearchResponseDto searchByKeyword(String keyword) {
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
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public ShopFavoritesResponseDto toShopFavoritesRespDto(Shop shop) {
        return ShopFavoritesResponseDto.builder()
                .id(shop.getId())
                .brand(shop.getBrand())
                .name(shop.getPlaceName())
                .address(shop.getRoadAddressName())
                .favoriteCnt(shop.getFavoriteCnt())
                .build();
    }
}
