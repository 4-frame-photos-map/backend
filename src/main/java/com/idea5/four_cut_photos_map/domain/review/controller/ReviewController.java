package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{reviewId}")
    public ResponseEntity<RsData> getReview(@PathVariable Long reviewId) {
        ResponseReviewDto responseReviewDto = reviewService.getReviewById(reviewId);

        RsData<ResponseReviewDto> body = new RsData<>(
                true,
                "리뷰 조회 완료",
                responseReviewDto
        );
        return new ResponseEntity<> (body, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<RsData> modifyReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext,
                                               @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.modify(memberContext.getId(), reviewId, reviewDto);

        log.debug("responseReviewDto = {}", responseReviewDto);

        RsData<Object> body = new RsData<>(
                true,
                "리뷰 수정 완료",
                null
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<RsData> deleteReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext) {
        reviewService.delete(memberContext.getId(), reviewId);

        RsData<Object> body = new RsData<>(
                true,
                "리뷰 삭제 완료",
                null
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
