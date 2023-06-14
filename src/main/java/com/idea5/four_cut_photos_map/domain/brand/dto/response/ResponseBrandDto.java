package com.idea5.four_cut_photos_map.domain.brand.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseBrandDto {
    private String brandName;
    private String filePath;

    public static ResponseBrandDto from(Brand brand) {
        return  ResponseBrandDto
                .builder()
                .brandName(brand.getBrandName())
                .filePath(brand.getFilePath())
                .build();
    }
}
