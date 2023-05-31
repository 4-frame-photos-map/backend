package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewInfoDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KakaoMapSearchApi;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.INVALID_SHOP_ID;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j

public class ShopService {
    private final ShopRepository shopRepository;
    private final KakaoMapSearchApi kakaoMapSearchApi;
    private final RedisDao redisDao;


    @Transactional(readOnly = true)
    public <T extends ResponseShop> List<T> findMatchingShops(List<KakaoMapSearchDto> apiShops, Class<T> responseClass) {
        List<T> resultShop = new ArrayList<>();
        for (KakaoMapSearchDto apiShop : apiShops) {
            // DB 주소체계가 도로명주소와 지번주소가 섞여있기 때문에 도로명주소와 지번주소 둘 다 인수로 전달
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
     * 카카오맵 API 장소 데이터의 지점명이나 주소명으로 DB Shop 객체를 조회하고, 장소명으로 2차 필터링하는 메서드입니다.
     * @param placeName 카카오맵 API 지점명
     * @param roadAddress 카카오맵 API 도로명주소
     * @param address 카카오맵 API 지번주소
     * @return DB Shop
     */
    public Shop compareWithPlaceNameOrAddress(String placeName, String roadAddress, String address) {
        String normalizedPlaceName = Util.removeSpace(placeName);
        String normalizedRoadAddress = Util.removeSpace(roadAddress);
        String normalizedAddress = Util.removeSpace(address);
        List<Shop> matchedShops = shopRepository.findByPlaceNameOrAddressIgnoringSpace(normalizedPlaceName, normalizedRoadAddress, normalizedAddress);

        Optional<Shop> exactMatch = matchedShops.stream()
                .filter(shop -> Util.removeSpace(shop.getPlaceName()).equals(normalizedPlaceName))
                .findFirst();

        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        log.info("Not Matched: DB shops ({} - {}), Kakao API shop ({} - {})",
                matchedShops.stream().map(Shop::getPlaceName).collect(Collectors.toList()),
                matchedShops.stream().map(Shop::getAddress).collect(Collectors.toList()),
                placeName,
                address
        );
        return null;
    }


    private void cacheShopInfoById(Shop dbShop, KakaoMapSearchDto apiShop) {
        String cacheKey = redisDao.getShopInfoKey(dbShop.getId());
        redisDao.setValues(
                cacheKey,
                String.join(",", apiShop.getPlaceUrl(), apiShop.getLatitude(), apiShop.getLongitude()),
                Duration.ofDays(1)
        );
    }

    private void cacheInvalidShopId(long shopId) {
        String cacheKey = redisDao.getInvalidShopIdKey();
        redisDao.addSet(cacheKey, String.valueOf(shopId));
    }

    public List<KakaoMapSearchDto> searchKakaoMapByKeyword(String keyword, Double userLat, Double userLng) {
        return kakaoMapSearchApi.searchByQueryWord(keyword, userLat, userLng);
    }

    public List<KakaoMapSearchDto> searchKakaoMapByBrand(String brand, Integer radius, Double userLat, Double userLng, Double mapLat, Double mapLng) {
        return kakaoMapSearchApi.searchByQueryWord(brand, radius, userLat, userLng, mapLat, mapLng);
    }

    public String convertMapCenterCoordToAddress(Double mapLat, Double mapLng) {
        return kakaoMapSearchApi.convertCoordinateToAddress(mapLat, mapLng);
    }

    public String calcDistFromUserLocation(Shop dbShop, Double userLat, Double userLng) {
        String[] cachedArr = kakaoMapSearchApi.getShopInfoFromCacheAndCalcDist(dbShop, userLat, userLng);
        if (cachedArr != null) {
            return cachedArr[3];
        } else {
            if (dbShop.getAddress() != null) {
                return kakaoMapSearchApi.convertAddressToCoordAndCalcDist(dbShop, userLat, userLng);
            } else {
                String[] apiShop = kakaoMapSearchApi.searchSingleShopByQueryWord(dbShop, userLat, userLng, dbShop.getPlaceName());
                if (apiShop != null) {
                    return apiShop[3];
                }
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
    }

    public ResponseShopDetail setResponseDto(Shop dbShop, Double userLat, Double userLng) {
        // 지점명으로 반환하는 지점 없을 시, 주소로 비교
        String[] queryWords = dbShop.getAddress() == null ?
                new String[]{dbShop.getPlaceName()} : new String[]{dbShop.getPlaceName(), dbShop.getAddress()};

        String[] apiShop = kakaoMapSearchApi.searchSingleShopByQueryWord(dbShop, userLat, userLng, queryWords);

        if (apiShop == null) {
            cacheInvalidShopId(dbShop.getId());
            throw new BusinessException(INVALID_SHOP_ID);
        }
        String placeUrl = apiShop[0];
        String placeLat = apiShop[1];
        String placeLng = apiShop[2];
        String distance = apiShop[3]; // userLat 또는 userLng가 null이면 Empty String 반환

        return ResponseShopDetail.of(dbShop, placeUrl, placeLat, placeLng, distance);
    }

    public FavoriteResponse setResponseDto(Favorite favorite, Double userLat, Double userLng) {
        String distance = calcDistFromUserLocation(favorite.getShop(), userLat, userLng);
        return FavoriteResponse.from(favorite, distance == null ? "" : distance);
    }

    public void reduceFavoriteCnt(Shop shop) {
        shop.setFavoriteCnt(shop.getFavoriteCnt() <= 0 ? 0 : shop.getFavoriteCnt() - 1);
        shopRepository.save(shop);
    }

    public void increaseFavoriteCnt(Shop shop){
        shop.setFavoriteCnt(shop.getFavoriteCnt()+1);
        shopRepository.save(shop);
    }

    @Transactional
    public void updateReviewInfo(ShopReviewInfoDto shopReviewInfo) {
        Shop shop = findById(shopReviewInfo.getShopId());
        shop.setReviewCnt(shopReviewInfo.getReviewCnt());
        shop.setStarRatingAvg(shopReviewInfo.getStarRatingAvg());
    }
}