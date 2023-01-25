package com.idea5.four_cut_photos_map.review.controller;

import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.util.Util;
import com.idea5.four_cut_photos_map.review.dto.request.WriteReviewDTO;
import com.idea5.four_cut_photos_map.review.entity.Review;
import com.idea5.four_cut_photos_map.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/list/{shopId}")
    public ResponseEntity<RsData> showReviews(@PathVariable Long shopId) {
        List<Review> reviews = reviewService.findAllByShopId(shopId);

        if(reviews.isEmpty()){
            return Util.spring.responseEntityOf(RsData.of(400, "상점에 리뷰가 없습니다."));
        }

        return Util.spring.responseEntityOf(RsData.of(200, "리뷰 조회를 성공하였습니다.", reviews));
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
