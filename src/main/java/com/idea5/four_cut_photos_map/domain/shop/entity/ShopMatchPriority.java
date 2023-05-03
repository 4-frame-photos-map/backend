package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.KakaoMapSearchDto;

/**
 * 데이터 중복일 경우 적용되는 우선순위
 * 도로명주소로 인한 데이터 중복 가능성이 높으므로, 지점명과 브랜드명을 기준으로 우선순위를 결정함
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

