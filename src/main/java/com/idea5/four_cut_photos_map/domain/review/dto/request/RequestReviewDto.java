package com.idea5.four_cut_photos_map.domain.review.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
