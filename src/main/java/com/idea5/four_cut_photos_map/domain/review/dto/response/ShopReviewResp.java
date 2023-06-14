package com.idea5.four_cut_photos_map.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberResp;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import lombok.*;

/**
 * 지점 전체 리뷰 조회 API 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShopReviewResp {
    private ReviewDto reviewInfo;   // 리뷰 정보
    private MemberResp memberInfo;  // 회원 정보
}
