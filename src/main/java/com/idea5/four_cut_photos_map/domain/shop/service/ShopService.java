package com.idea5.four_cut_photos_map.domain.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KeywordSearchKakaoApi;
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
            // DB에서 장소명으로 Shop 조회(비교)
            Shop dbShop = shopRepository.findByPlaceName(apiShop.getPlaceName()).orElse(null);

            if(dbShop != null) {
                // dbShop(Entity) -> responseShop(DTO) 변환
                ResponseShop responseShop = ResponseShop.from(dbShop);
                // apiShop(카카오 맵 API 응답 객체)의 위도, 경도 responseShop(응답 DTO 객체)에 저장
                responseShop.setLongitude(Double.parseDouble(apiShop.getLongitude()));
                responseShop.setLatitude(Double.parseDouble(apiShop.getLatitude()));
                responseShops.add(responseShop);
            }
        }

        if(responseShops.isEmpty()) {throw new BusinessException(SHOP_NOT_FOUND);}

        return responseShops;
    }


    // todo : Review, 찜 추가
    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
        ResponseShopDetail shopDto = ResponseShopDetail.of(shop, distance);
        return shopDto;

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

    public ResponseFavoriteShop toFavoriteShopDto(Shop shop) {
        return ResponseFavoriteShop.builder()
                .id(shop.getId())
                .brand(shop.getBrand())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .favoriteCnt(shop.getFavoriteCnt())
                .build();
    }
}
