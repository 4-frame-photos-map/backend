package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> shopTitles;


    static public ResponseShopBrand of(Shop dbShop, KakaoMapSearchDto apiShop, Brand brand){
        ResponseBrandDto brandDto = ResponseBrandDto.builder()
                .brandName(brand.getBrandName())
                .filePath(brand.getFilePath())
                .build();

        return ResponseShopBrand.builder()
                .id(dbShop.getId())
                .placeName(dbShop.getPlaceName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .starRatingAvg(dbShop.getStarRatingAvg())
                .reviewCnt(dbShop.getReviewCnt())
                .favoriteCnt(dbShop.getFavoriteCnt())
                .brand(brandDto)
                .shopTitles(new ArrayList<>())
                .build();
    }
}