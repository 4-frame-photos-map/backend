package com.idea5.four_cut_photos_map.domain.shop.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewResp;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 상세 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopDetail {
    private long id;
    private String placeName;
    private String distance;
    private double starRatingAvg;
    private int reviewCnt;
    private int favoriteCnt;
    private boolean isFavorite;
    private List<String> shopTitles;
    private String latitude;
    private String longitude;
    private String placeUrl;
    private List<ShopReviewResp> recentReviews;


    public static ResponseShopDetail of(Shop dbShop, String placeUrl, String placeLat, String placeLng,  String distance){
        return ResponseShopDetail.builder()
                .id(dbShop.getId())
                .placeName(dbShop.getPlaceName())
                .latitude(placeLat)
                .longitude(placeLng)
                .distance(distance)
                .placeUrl(placeUrl)
                .starRatingAvg(dbShop.getStarRatingAvg())
                .reviewCnt(dbShop.getReviewCnt())
                .favoriteCnt(dbShop.getFavoriteCnt())
                .shopTitles(new ArrayList<>())
                .build();
    }
}