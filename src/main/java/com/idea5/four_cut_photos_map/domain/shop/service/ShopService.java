package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.entity.ShopMatchPriority;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KakaoMapSearchApi;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.INVALID_SHOP_ID;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j

public class ShopService {
    private final ShopRepository shopRepository;
    private final FavoriteRepository favoriteRepository;
    private final KakaoMapSearchApi kakaoMapSearchApi;
    private final RedisDao redisDao;


    @Transactional(readOnly = true)
    public <T extends ResponseShop> List<T> findMatchingShops(List<KakaoMapSearchDto> apiShops, Class<T> responseClass) {
        List<T> resultShop = new ArrayList<>();
        for (KakaoMapSearchDto apiShop : apiShops) {
            // 도로명주소 비교로 반환하는 지점 없을 시, 지번주소로 비교
            Shop dbShop = compareWithPlaceNameOrAddress(apiShop.getPlaceName(), apiShop.getRoadAddressName(), apiShop.getAddressName());

            if (dbShop != null) {
                log.info("Matched: DB shop ({} - {}), Kakao API shop ({} - {} - {})",
                        dbShop.getPlaceName(), dbShop.getAddress(), apiShop.getPlaceName(),
                        apiShop.getRoadAddressName(), apiShop.getAddressName()
                );

                cacheShopInfoById(dbShop, apiShop);

                if (responseClass.equals(ResponseShopKeyword.class)) {
                    ResponseShopKeyword responseShop = ResponseShopKeyword.of(dbShop, apiShop, dbShop.getBrand());
                    resultShop.add(responseClass.cast(responseShop));
                } else if (responseClass.equals(ResponseShopBrand.class)) {
                    ResponseShopBrand responseShop = ResponseShopBrand.of(dbShop, apiShop, dbShop.getBrand());
                    resultShop.add(responseClass.cast(responseShop));
                }
            }
        }
        return resultShop;
    }

    /**
     * 지점명 일치여부나 주소명 포함여부로 비교하여 Kakao API Shop과 일치하는 DB Shop 객체 반환
     * 중복 발생 시 주소 중복일 확률이 높으므로, 지점명과 브랜드명을 기준으로 우선순위를 결정함
     * @param placeName (카카오 API 지점명)
     * @param addresses (카카오 API 도로명주소, 지번주소)
     * @return DB Shop
     */
    @Transactional(readOnly = true)
    public Shop compareWithPlaceNameOrAddress(String placeName, String... addresses) {
        for (String address : addresses) {
            List<Shop> matchedShops = shopRepository.findDistinctByPlaceNameOrAddressContaining(
                    placeName,
                    address
            );
            if (matchedShops.size() == 1) {
                return matchedShops.get(0);
            } else if (matchedShops.size() > 1) {
                Shop matchedShop = compareMatchingShops(placeName, matchedShops);
                if (matchedShop != null) return matchedShop;
            }
            log.info("Not Matched: DB shops ({} - {}), Kakao API shop ({} - {})",
                    matchedShops.stream().map(Shop::getPlaceName).collect(Collectors.toList()),
                    matchedShops.stream().map(Shop::getAddress).collect(Collectors.toList()),
                    placeName,
                    address
            );
        }
        return null;
    }

    private Shop compareMatchingShops(String apiPlaceName, List<Shop> dbShops) {
        return Arrays.stream(ShopMatchPriority.values())
                .flatMap(priority -> dbShops.stream()
                        .filter(dbShop -> priority.isMatchedShop(dbShop, apiPlaceName)))
                .findFirst()
                .orElse(null);
    }

    private void cacheShopInfoById(Shop dbShop, KakaoMapSearchDto apiShop) {
        String cacheKey = redisDao.getShopInfoKey(dbShop.getId());
        redisDao.setValues(
                cacheKey,
                String.join(",", apiShop.getPlaceUrl(), apiShop.getLatitude(), apiShop.getLongitude()),
                Duration.ofDays(1)
        );
    }

    public List<KakaoMapSearchDto> searchKakaoMapByKeyword(String keyword, Double userLat, Double userLng) {
        return kakaoMapSearchApi.searchByQueryWord(keyword, userLat, userLng);
    }

    public List<KakaoMapSearchDto> searchKakaoMapByBrand(String brand, Integer radius, Double userLat, Double userLng, Double mapLat, Double mapLng) {
            return kakaoMapSearchApi.searchByQueryWord(brand, radius, userLat, userLng, mapLat, mapLng);
    }

    public String[] searchSingleShopByQueryWord(Shop dbShop, Double userLat, Double userLng) {
        return kakaoMapSearchApi.searchSingleShopByQueryWord(dbShop, userLat, userLng);
    }

    @Transactional(readOnly = true)
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public ResponseShopDetail setResponseDto(Shop dbShop, Double userLat, Double userLng) {
        // 지점명으로 반환하는 지점 없을 시, 주소로 비교
        String[] apiShop = searchSingleShopByQueryWord(dbShop, userLat, userLng);

        if (apiShop == null) throw new BusinessException(INVALID_SHOP_ID);
        String placeUrl = apiShop[0];
        String placeLat = apiShop[1];
        String placeLng = apiShop[2];
        String distance = apiShop[3];

        return ResponseShopDetail.of(dbShop, placeUrl, placeLat, placeLng, distance);
    }

    public FavoriteResponse setResponseDto(Favorite favorite, Double userLat, Double userLng) {
        // 지점명으로 반환하는 지점 없을 시, 주소로 비교
        String[] apiShop = searchSingleShopByQueryWord(favorite.getShop(), userLat, userLng);

        if (apiShop == null) {
            Shop shopWithInvalidId = favorite.getShop();
            favoriteRepository.deleteById(favorite.getId());
            reduceFavoriteCnt(shopWithInvalidId);
            return null;
        }

        String distance = apiShop[3];

        return FavoriteResponse.from(favorite, distance);
    }


    @Transactional(readOnly = true)
    public ResponseShopBriefInfo setResponseDto(long id, String placeName, String distance) {
        Shop dbShop = findById(id);
        return ResponseShopBriefInfo.of(dbShop, placeName, distance);
    }

    public void reduceFavoriteCnt(Shop shop) {
        shop.setFavoriteCnt(shop.getFavoriteCnt() <= 0 ? 0 : shop.getFavoriteCnt() - 1);
        shopRepository.save(shop);
    }

    public void increaseFavoriteCnt(Shop shop){
        shop.setFavoriteCnt(shop.getFavoriteCnt()+1);
        shopRepository.save(shop);
    }

    public String convertMapCenterCoordToAddress(Double mapLat, Double mapLng) {
        return kakaoMapSearchApi.convertCoordinateToAddress(mapLat, mapLng);
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