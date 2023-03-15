package com.idea5.four_cut_photos_map.domain.review.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestReviewDto {
    @Max(value = 5, message = "별점을 1 ~ 5점으로 선택해주세요.")
    @Min(value = 1, message = "별점을 1 ~ 5점으로 선택해주세요.")
    private int starRating;

    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;

    private String purity;

    private String retouch;

    private String item;

    public Review toEntity(){
        return Review.builder()
                .starRating(getStarRating())
                .content(getContent())
                .purity(getPurity() == null ? PurityScore.UNSELECTED : PurityScore.valueOf(getPurity()))
                .retouch(getRetouch() == null ? RetouchScore.UNSELECTED : RetouchScore.valueOf(getRetouch()))
                .item(getItem() == null ? ItemScore.UNSELECTED : ItemScore.valueOf(getItem()))
                .build();
    }
}
