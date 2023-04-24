package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseShopBrand {
    String mapCenterAddress;
    List<ResponseShop> shops = new ArrayList<>();
}