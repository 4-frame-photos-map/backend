package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{shopId}")
    public ResponseEntity<RsData> getShopReviews(@PathVariable Long shopId) {
        List<ResponseReviewDto> reviews = reviewService.findAllByShopId(shopId);

        RsData<List<ResponseReviewDto>> body = new RsData<>(
                true,
                "상점 리뷰 정보 조회 성공",
                reviews
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
