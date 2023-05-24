package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 키워드 조회 응답 DTO
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopKeyword extends ResponseShop {
    private String placeAddress;

    static public ResponseShopKeyword of(Shop dbShop, KakaoMapSearchDto apiShop, Brand brand){
        ResponseBrandDto brandDto = ResponseBrandDto.builder()
                .brandName(brand.getBrandName())
                .filePath(brand.getFilePath())
                .build();

        return ResponseShopKeyword.builder()
                .id(dbShop.getId())
                .placeName(dbShop.getPlaceName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .starRatingAvg(dbShop.getStarRatingAvg())
                .reviewCnt(dbShop.getReviewCnt())
                .favoriteCnt(dbShop.getFavoriteCnt())
                .placeAddress(apiShop.getRoadAddressName())
                .brand(brandDto)
                .build();
    }
}