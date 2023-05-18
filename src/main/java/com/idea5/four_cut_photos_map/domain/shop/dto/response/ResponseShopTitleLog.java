package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 지점 칭호 로그 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopTitleLog {
    private Long id;
    private String placeName;


    public static ResponseShopTitleLog from(Shop shop) {
        return ResponseShopTitleLog.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .build();
    }
}
