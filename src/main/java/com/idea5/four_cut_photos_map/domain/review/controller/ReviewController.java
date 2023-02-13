package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.request.WriteReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ShopService shopService;

    @GetMapping("/{shopId}")
    public ResponseEntity<RsData> getShopReviews(@PathVariable Long shopId) {
        List<ResponseReviewDto> reviews = reviewService.searchAllReviewsInTheStore(shopId);

        RsData<List<ResponseReviewDto>> body = new RsData<>(
                true,
                "상점 리뷰 정보 조회 성공",
                reviews
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/{shopId}")
    public ResponseEntity<RsData> writeReview(@PathVariable Long shopId,
                                         @AuthenticationPrincipal MemberContext memberContext,
                                         @Valid @RequestBody WriteReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.write(reviewDto, shopId, memberContext.getId());

        RsData<ResponseReviewDto> body = new RsData<>(
                true,
                "상점 리뷰 작성 성공",
                responseReviewDto
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PatchMapping("/{shopId}")
    public ResponseEntity<RsData> modifyReview(@PathVariable Long shopId,
                                               @AuthenticationPrincipal MemberContext memberContext,
                                               @Valid @RequestBody WriteReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.modify(reviewDto, shopId, memberContext.getId());

        RsData<ResponseReviewDto> body = new RsData<>(
                true,
                "상점 리뷰 수정 성공",
                responseReviewDto
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
