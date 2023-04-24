package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KakaoMapSearchApi;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.INVALID_SHOP_ID;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j

public class ShopService {
    private final ShopRepository shopRepository;
    private final FavoriteRepository favoriteRepository;
    private final KakaoMapSearchApi kakaoMapSearchApi;


    @Transactional(readOnly = true)
    public <T extends ResponseShop> List<T> compareWithDbShops(List<KakaoMapSearchDto> apiShops, Class<T> responseClass) {
        List<T> resultShop = new ArrayList<>();
        for (KakaoMapSearchDto apiShop: apiShops) {
            Shop dbShop = compareRoadAddressNameAndPlaceName(apiShop);

            if(dbShop != null) {
                if(responseClass.equals(ResponseShopKeyword.class)) {
                    ResponseShopKeyword responseShop = ResponseShopKeyword.of(dbShop, apiShop, dbShop.getBrand());
                    resultShop.add(responseClass.cast(responseShop));
                } else if(responseClass.equals(ResponseShopBrand.class)){
                    ResponseShopBrand responseShop = ResponseShopBrand.of(dbShop, apiShop, dbShop.getBrand());
                    resultShop.add(responseClass.cast(responseShop));
                }
            }
        }
        return resultShop;
    }

    @Transactional(readOnly = true)
    public Shop compareRoadAddressNameAndPlaceName(KakaoMapSearchDto apiShop) {
        return shopRepository.findDistinctByRoadAddressNameAndPlaceNameContaining(
                apiShop.getRoadAddressName(),
                apiShop.getPlaceName().split(" ")[0]
        ).orElse(null);
    }

    public List<KakaoMapSearchDto> searchKakaoMapByKeyword(String keyword, Double userLat, Double userLng) {
        return kakaoMapSearchApi.searchByQueryWord (keyword, userLat, userLng);
    }

    public List<KakaoMapSearchDto> searchKakaoMapByBrand(String brand, Double userLat, Double userLng, Double mapLat, Double mapLng) {
        if (userLat == mapLat && userLng == mapLng) {
            return kakaoMapSearchApi.searchByQueryWord (brand, userLat, userLng);
        }
        else {
            return kakaoMapSearchApi.searchByQueryWord (brand, userLat, userLng, mapLat, mapLng);
        }
    }

    @Transactional(readOnly = true)
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public ResponseShopDetail renameShopAndSetResponseDto(Shop dbShop, String distance) {
        String[] apiShop = kakaoMapSearchApi.searchByRoadAddressName(dbShop.getRoadAddressName(), dbShop.getPlaceName());

        if(apiShop == null) throw new BusinessException(INVALID_SHOP_ID);
        String placeName = apiShop[0];
        String placeUrl = apiShop[1];
        String placeLng = apiShop[2];
        String placeLat = apiShop[3];

        return ResponseShopDetail.of(dbShop, placeName, placeUrl, placeLng, placeLat, distance);
    }

    public FavoriteResponse renameShopAndSetResponseDto(Favorite favorite, Double userLat, Double userLng) {
        String[] apiShop = kakaoMapSearchApi.searchByRoadAddressName(
                favorite.getShop().getRoadAddressName(),
                favorite.getShop().getPlaceName(),
                userLat,
                userLng
        );

        if(apiShop == null) {
            Shop shopWithInvalidId = favorite.getShop();
            favoriteRepository.deleteById(favorite.getId());
            reduceFavoriteCnt(shopWithInvalidId);
            return null;
        }

        String placeName = apiShop[0];
        String distance = apiShop[1];

        return FavoriteResponse.from(favorite, placeName, distance);
    }


    @Transactional(readOnly = true)
    public ResponseShopBriefInfo setResponseDto (long id, String placeName, String distance) {
        Shop dbShop = findById(id);
        return ResponseShopBriefInfo.of(dbShop, placeName, distance);
    }

    public void reduceFavoriteCnt(Shop shop){
        shop.setFavoriteCnt(shop.getFavoriteCnt() <= 0 ? 0 : shop.getFavoriteCnt() - 1);
        shopRepository.save(shop);
    }

    @Transactional
    public void reduceFavoriteCnt(Long shopId){
        Shop shop = findById(shopId);
        shop.setFavoriteCnt(shop.getFavoriteCnt() <= 0 ? 0 : shop.getFavoriteCnt() - 1);
    }

    public void increaseFavoriteCnt(Shop shop){
        shop.setFavoriteCnt(shop.getFavoriteCnt()+1);
        shopRepository.save(shop);
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