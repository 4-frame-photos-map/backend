package com.idea5.four_cut_photos_map.domain.review.dto.request;

import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class WriteReviewDto {
    @Max(value = 5, message = "별점을 1 ~ 5점으로 선택해주세요.")
    @Min(value = 1, message = "별점을 1 ~ 5점으로 선택해주세요.")
    private int starRating;

    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;

    @ColumnDefault("")
    private String purity;

    @ColumnDefault("")
    private String retouch;

    @ColumnDefault("")
    private String item;

    @ColumnDefault("")
    private String distance;

}
