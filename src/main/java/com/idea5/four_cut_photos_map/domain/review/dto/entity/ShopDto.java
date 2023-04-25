package com.idea5.four_cut_photos_map.domain.review.dto.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShopDto {
    private Long id;            // 가게 번호
    private String brand;       // 브랜드 명
    private String placeName;        // 가게 이름
}
