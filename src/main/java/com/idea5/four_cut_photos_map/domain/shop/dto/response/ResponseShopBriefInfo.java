package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 간단 조회 응답 DTO (Map Marker 모달용)
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopBriefInfo {
    private long id;
    private String placeName;
    private String distance;
    private String placeUrl;
    private double starRatingAvg;
    private double reviewCnt;
    private boolean canBeAddedToFavorites;


    static public ResponseShopBriefInfo of(long id, String placeName, String distance, String placeUrl){
        return ResponseShopBriefInfo.builder()
                .id(id)
                .placeName(placeName)
                .distance(distance)
                .placeUrl(placeUrl)
//                .ratingAvg()
//                .reviewCnt()
                .build();
    }
}
