package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 간단 조회 응답 DTO (Map Marker 모달용)
 */
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopBriefInfo {
    private long id;
    private String placeName;
    private String distance;
    private String placeUrl;
    private double starRatingAvg;
    private int reviewCnt;
    private boolean isFavorite;


    static public ResponseShopBriefInfo of(Shop dbShop, String placeName, String placeUrl, String distance){
        return ResponseShopBriefInfo.builder()
                .id(dbShop.getId())
                .placeName(placeName)
                .distance(distance)
                .placeUrl(placeUrl)
                .starRatingAvg(dbShop.getStarRatingAvg())
                .reviewCnt(dbShop.getReviewCnt())
                .build();
    }
}
