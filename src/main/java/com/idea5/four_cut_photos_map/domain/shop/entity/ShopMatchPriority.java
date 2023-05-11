package com.idea5.four_cut_photos_map.domain.shop.entity;


import java.util.List;

/**
 * Kakao Maps API 지점과 일치하는 DB 지점 조회 시 중복되는 지점이 있을 경우, 이를 해결하기 위해 적용되는 우선순위
 */
public enum ShopMatchPriority {
    PLACE_NAME,
    BRAND_NAME_CONTAINS;

    public boolean isMatchedShop(String dbPlaceName, String apiPlaceName, List<Shop> dbShops) {
        switch (this) {
            case PLACE_NAME:
                return dbPlaceName.equals(apiPlaceName);
            case BRAND_NAME_CONTAINS:
                String apiBrandName = apiPlaceName.split(" ")[0];
                if(dbPlaceName.contains(apiBrandName)) {
                    return dbShops.stream()
                            .filter(dbShop -> dbShop.getPlaceName().contains(apiBrandName))
                            .count() == 1;
                }
            default:
                return false;
        }
    }
}