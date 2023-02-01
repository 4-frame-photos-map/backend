package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.entity.Review;
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
        List<Review> reviews = reviewService.findAllByShopId(shopId);

        RsData<List<Review>> body = new RsData<>(
                true, "리뷰 정보 조회 성공", reviews
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //@PostMapping("/write/{shopId}")
    //public ResponseEntity<RsData> writeReview(@PathVariable Long shopId, @RequestBody WriteReviewDTO reviewDTO) {
    //    if (reviewDTO.isNotValid()) {
    //        return Util.spring.responseEntityOf(RsData.of(400, "사용자가 보낸 데이터가 올바르지 않습니다."));
    //    }
    //    reviewService.writeReview(shopId, reviewDTO);

    //    return null;
    //}

}
