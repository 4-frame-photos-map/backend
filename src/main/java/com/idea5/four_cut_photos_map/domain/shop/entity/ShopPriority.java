package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;

/**
 * 데이터 중복일 경우 Shop 우선순위
 * 도로명주소로 인한 데이터 중복 가능성이 높으므로, 지점명과 브랜드명 위주로 우선순위를 결정함
 */
public enum ShopPriority {
    PLACE_NAME_AND_ROAD_ADDRESS_NAME,
    PLACE_NAME_AND_ROAD_ADDRESS_NAME_CONTAINS,
    PLACE_NAME,
    BRAND_NAME_CONTAINS;
    public boolean isMatchedShop(Shop dbShop, KakaoMapSearchDto apiShop) {
        switch (this) {
            case PLACE_NAME_AND_ROAD_ADDRESS_NAME:
                return dbShop.getPlaceName().equals(apiShop.getPlaceName()) &&
                        dbShop.getRoadAddressName().equals(apiShop.getRoadAddressName());
            case PLACE_NAME_AND_ROAD_ADDRESS_NAME_CONTAINS:
                return dbShop.getPlaceName().equals(apiShop.getPlaceName()) &&
                        dbShop.getRoadAddressName().contains(apiShop.getRoadAddressName());
            case PLACE_NAME: // DB 도로명주소 Null인 경우 고려
                return dbShop.getPlaceName().equals(apiShop.getPlaceName());
            case BRAND_NAME_CONTAINS:
                return dbShop.getPlaceName().contains(apiShop.getPlaceName().split(" ")[0]);
            default:
                return false;
        }
    }
}

