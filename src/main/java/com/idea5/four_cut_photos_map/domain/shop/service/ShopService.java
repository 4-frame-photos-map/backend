package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBrand;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopKeyword;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KeywordSearchKakaoApi;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j

public class ShopService {
    private final ShopRepository shopRepository;
    private final KeywordSearchKakaoApi keywordSearchKakaoApi;
    private static final String DEFAULT_QUERY_WORD = "즉석사진";

    public List<ResponseShopKeyword> compareWithDbShops(List<KakaoResponseDto> apiShops) {
        List<ResponseShopKeyword> resultShop = new ArrayList<>();

        for (KakaoResponseDto apiShop: apiShops) {
            List<Shop> dbShops = compareRoadAddressName(apiShop);

            if(dbShops.isEmpty()) continue;

            Shop dbShop = dbShops.size() == 1 ?  dbShops.get(0) : comparePlaceName(apiShop.getPlaceName(), dbShops);

            if(dbShop != null) {
                ResponseShopKeyword responseShopKeyword = ResponseShopKeyword.of(dbShop, apiShop);
                resultShop.add(responseShopKeyword);
            }
        }
        return resultShop;
    }

    public List<ResponseShopBrand> compareWithDbShops(List<KakaoResponseDto> apiShops, List<ResponseShopBrand> resultShop) {
        for (KakaoResponseDto apiShop: apiShops) {
            List<Shop> dbShops = compareRoadAddressName(apiShop);

            if(dbShops.isEmpty()) continue;

            Shop dbShop = dbShops.size() == 1 ?  dbShops.get(0) : comparePlaceName(apiShop.getPlaceName(), dbShops);

            if(dbShop != null) {
                ResponseShopBrand responseShopMarker = ResponseShopBrand.of(dbShop, apiShop);
                resultShop.add(responseShopMarker);
            }
        }
        return resultShop;
    }

    private List<Shop> compareRoadAddressName(KakaoResponseDto apiShop) {
        List<Shop> dbShops = shopRepository.findDistinctByRoadAddressName(apiShop.getRoadAddressName());
        return dbShops;
    }

    private Shop comparePlaceName(String apiShopPlaceName, List<Shop> dbShops) {
        return dbShops.stream()
                .filter(dbShop -> apiShopPlaceName.contains(dbShop.getPlaceName()))
                .findFirst()
                .orElse(null);
    }

    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop dbShop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));

        String apiShopPlaceName = searchByRoadAddressName(dbShop);

        ResponseShopDetail shopDto = ResponseShopDetail.of(dbShop, distance, apiShopPlaceName);
        return shopDto;
    }

    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public List<KakaoResponseDto> searchByKeyword(RequestKeywordSearch keywordSearch) {
        return keywordSearchKakaoApi.searchByQueryWord (
                keywordSearch.getKeyword() + DEFAULT_QUERY_WORD,
                keywordSearch.getLongitude(),
                keywordSearch.getLatitude(),
                false
        );
    }

    public List<KakaoResponseDto> searchByBrand(RequestBrandSearch brandSearch) {
        if(ObjectUtils.isEmpty(brandSearch.getBrand())) brandSearch.setBrand(DEFAULT_QUERY_WORD);

        return keywordSearchKakaoApi.searchByQueryWord (
                brandSearch.getBrand(),
                brandSearch.getLongitude(),
                brandSearch.getLatitude(),
                true
        );
    }

    public String searchByRoadAddressName(Shop dbShop) {
        return keywordSearchKakaoApi.searchByRoadAddressName (
                dbShop.getRoadAddressName() + DEFAULT_QUERY_WORD
        );
    }

    public boolean isRepresentativeBrand(String requestBrand) {
        return Arrays.stream(Brand.Names)
                .anyMatch(representative -> representative.equals(requestBrand));
    }

    // 브랜드별 Map Marker
//    public List<ResponseShopMarker> searchMarkers(RequestShop shop, String brandName) {
//        List<KakaoKeywordResponseDto> kakaoShops = keywordSearchKakaoApi.searchByQueryWord(shop, brandName);
//        List<ShopDto> dbShops = findByBrand(brandName);
//        List<ResponseShopMarker> resultShops = new ArrayList<>();
//
//        for (KakaoKeywordResponseDto kakaoShop : kakaoShops) {
//            for (ShopDto dbShop : dbShops) {
//                if (kakaoShop.getRoadAddressName().equals(dbShop.getRoadAddressName())) {
//                    ResponseShopMarker responseShopMarker = ResponseShopMarker.of(kakaoShop);
//                    responseShopMarker.setId(dbShop.getId());
//                    // 상점이 칭호를 보유했으면 추가
//                    if(shopTitleLogService.existShopTitles(dbShop.getId())){
//                        responseShopMarker.setShopTitles(shopTitleLogService.getShopTitles(dbShop.getId()));
//                    }
//                    resultShops.add(responseShopMarker);
//                    break;
//                }
//            }
//        }
//        return resultShops;
//    }
}