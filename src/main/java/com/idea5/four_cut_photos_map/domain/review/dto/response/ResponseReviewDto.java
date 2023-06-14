package com.idea5.four_cut_photos_map.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ShopDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseReviewDto {

    // Review 관련 정보
    private ReviewDto reviewInfo;

    // Member 관련 정보
    private MemberDto memberInfo;

    // Shop 관련 정보
    private ShopDto shopInfo;
}
