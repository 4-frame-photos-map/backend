package com.idea5.four_cut_photos_map.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ShopDto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseMemberReviewDto {

    // Review 관련 정보
    private ReviewDto reviewInfo;

    // Shop 관련 정보
    private ShopDto shopInfo;
}
