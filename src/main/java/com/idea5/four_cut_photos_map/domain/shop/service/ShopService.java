package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.kakao.KakaoMapSearchDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestKeywordSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopBrand;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopKeyword;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KakaoMapSearchApi;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j

public class ShopService {
    public static final String DEFAULT_QUERY_WORD = "즉석사진";
    private final ShopRepository shopRepository;
    private final KakaoMapSearchApi kakaoMapSearchApi;

    public List<ResponseShopKeyword> compareWithDbShops(List<KakaoMapSearchDto> apiShops) {
        List<ResponseShopKeyword> resultShop = new ArrayList<>();

        for (KakaoMapSearchDto apiShop: apiShops) {
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

    public List<ResponseShopBrand> compareWithDbShops(List<KakaoMapSearchDto> apiShops, List<ResponseShopBrand> resultShop) {
        for (KakaoMapSearchDto apiShop: apiShops) {
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

    private List<Shop> compareRoadAddressName(KakaoMapSearchDto apiShop) {
        List<Shop> dbShops = shopRepository.findDistinctByRoadAddressName(apiShop.getRoadAddressName());
        return dbShops;
    }

    private Shop comparePlaceName(String apiShopPlaceName, List<Shop> dbShops) {
        return dbShops.stream()
                .filter(dbShop -> apiShopPlaceName.contains(dbShop.getPlaceName()))
                .findFirst()
                .orElse(null);
    }

    public ResponseShopDetail renameShopAndGetPlaceUrl(Shop dbShop, String distance) {
        String[] apiShop = searchByRoadAddressName(dbShop);
        String apiPlaceName = apiShop[0];
        String apiPlaceUrl = apiShop[1];

        if(apiPlaceName != null) dbShop.setPlaceName(apiPlaceName);

        ResponseShopDetail shopDto = ResponseShopDetail.of(dbShop, distance, apiPlaceUrl);
        return shopDto;
    }

    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public List<KakaoMapSearchDto> searchByKeyword(RequestKeywordSearch keywordSearch) {
        return kakaoMapSearchApi.searchByQueryWord (
                keywordSearch.getKeyword() + DEFAULT_QUERY_WORD,
                keywordSearch.getLongitude(),
                keywordSearch.getLatitude(),
                false
        );
    }

    public List<KakaoMapSearchDto> searchByBrand(RequestBrandSearch brandSearch) {
        if(ObjectUtils.isEmpty(brandSearch.getBrand())) brandSearch.setBrand(DEFAULT_QUERY_WORD);

        return kakaoMapSearchApi.searchByQueryWord (
                brandSearch.getBrand(),
                brandSearch.getLongitude(),
                brandSearch.getLatitude(),
                true
        );
    }

    public String[] searchByRoadAddressName(Shop dbShop) {
        return kakaoMapSearchApi.searchByRoadAddressName (
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