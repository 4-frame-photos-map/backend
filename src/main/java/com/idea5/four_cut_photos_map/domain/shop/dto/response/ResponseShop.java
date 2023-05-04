package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 키워드 조회, 전체/브랜드별 조회 공통 응답 DTO
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShop {
    private Long id;
    private String placeName;
    private String longitude;
    private String latitude;
    private String distance;
    private String placeUrl;
    private double starRatingAvg;
    private int reviewCnt;
    private int favoriteCnt;
    private boolean isFavorite;
    private ResponseBrandDto brand;

    static public ResponseShop of(Shop dbShop, KakaoMapSearchDto apiShop, Brand brand){
        ResponseBrandDto brandDto = ResponseBrandDto.builder()
                .id(brand.getId())
                .brandName(brand.getBrandName())
                .filePath(brand.getFilePath())
                .build();

        return ResponseShop.builder()
                .id(dbShop.getId())
                .placeName(dbShop.getPlaceName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .placeUrl(apiShop.getPlaceUrl())
                .starRatingAvg(dbShop.getStarRatingAvg())
                .reviewCnt(dbShop.getReviewCnt())
                .favoriteCnt(dbShop.getFavoriteCnt())
                .brand(brandDto)
                .build();
    }
}