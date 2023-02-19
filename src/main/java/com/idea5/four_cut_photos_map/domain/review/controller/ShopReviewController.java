package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{shopId}/reviews")
    public ResponseEntity<RsData> getShopReviews(@PathVariable Long shopId) {
        List<ResponseReviewDto> reviews = reviewService.getAllShopReviews(shopId);

        RsData<List<ResponseReviewDto>> body = new RsData<>(
                true,
                "상점의 모든 리뷰 조회 완료",
                reviews
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/{shopId}/reviews")
    public ResponseEntity<RsData> writeReview(@PathVariable Long shopId,
                                              @AuthenticationPrincipal MemberContext memberContext,
                                              @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.write(memberContext.getId(), shopId, reviewDto);

        log.debug("responseReviewDto = {}", responseReviewDto);

        RsData<ResponseReviewDto> body = new RsData<>(
                true,
                "상점 리뷰 작성 성공",
                responseReviewDto
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
