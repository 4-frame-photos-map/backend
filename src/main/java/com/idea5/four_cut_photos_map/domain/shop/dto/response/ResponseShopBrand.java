package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 전체/브랜드 별 조회 응답 DTO
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopBrand extends ResponseShop {

    static public ResponseShopBrand of(Shop dbShop, KakaoMapSearchDto apiShop, Brand brand){
        ResponseBrandDto brandDto = ResponseBrandDto.builder()
                .id(brand.getId())
                .brandName(brand.getBrandName())
                .filePath(brand.getFilePath())
                .build();

        return ResponseShopBrand.builder()
                .id(dbShop.getId())
                .placeName(apiShop.getPlaceName())
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