package com.idea5.four_cut_photos_map.domain.shop.entity;


import java.util.List;

/**
 * Kakao Maps API 지점과 일치하는 DB 지점 조회 시 중복되는 지점이 있을 경우, 이를 해결하기 위해 적용되는 우선순위
 * @see com.idea5.four_cut_photos_map.domain.shop.service.ShopService#compareMatchingShops(String, List)
 */
public enum ShopMatchPriority {
    PLACE_NAME,
    BRAND_NAME_CONTAINS;

    public boolean isMatchedShop(String dbPlaceName, String apiPlaceName) {
        switch (this) {
            case PLACE_NAME:
                return dbPlaceName.equals(apiPlaceName);
            case BRAND_NAME_CONTAINS:
                return dbPlaceName.contains(apiPlaceName.split(" ")[0]);
            default:
                return false;
        }
    }
}

