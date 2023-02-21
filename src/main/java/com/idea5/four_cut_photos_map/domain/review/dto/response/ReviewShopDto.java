package com.idea5.four_cut_photos_map.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewShopDto {
    private Long id;            // 가게 번호
    private String brand;       // 브랜드 명
    private String placeName;        // 가게 이름
    private String roadAddressName;     // 주소
}
