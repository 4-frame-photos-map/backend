package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

/**
 * 키워드 조회, 전체/브랜드별 조회 공통 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShop {
    private Long id;
    private String placeName;
    private String roadAddressName;
    private String longitude;
    private String latitude;
    private String distance;
    private String placeUrl;
    private boolean canBeAddedToFavorites;

    static public ResponseShop of(Shop dbShop, KakaoMapSearchDto apiShop){
        return ResponseShop.builder()
                .id(dbShop.getId())
                .placeName(apiShop.getPlaceName())
                .roadAddressName(apiShop.getRoadAddressName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .placeUrl(apiShop.getPlaceUrl())
                .build();
    }
}