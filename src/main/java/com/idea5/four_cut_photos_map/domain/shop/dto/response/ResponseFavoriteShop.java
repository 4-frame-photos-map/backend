package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 찜 목록 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseFavoriteShop {
    private Long id;
    private String placeName;
    private String distance;
    private double starRatingAvg;
    private int reviewCnt;
    private int favoriteCnt;

    public static ResponseFavoriteShop from(Shop shop, String placeName, String distance) {
        return ResponseFavoriteShop.builder()
                .id(shop.getId())
                .placeName(placeName)
                .distance(distance)
                .starRatingAvg(shop.getStarRatingAvg())
                .reviewCnt(shop.getReviewCnt())
                .favoriteCnt(shop.getFavoriteCnt())
                .build();
    }
}
