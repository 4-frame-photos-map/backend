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

    @GetMapping("/{review-id}")
    public ResponseEntity<RsData> getReview(@PathVariable("review-id") Long reviewId) {
        ResponseReviewDto responseReviewDto = reviewService.getReviewById(reviewId);

        return new ResponseEntity<> (
                new RsData<>(true, "리뷰 조회 완료", responseReviewDto),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{review-id}")
    public ResponseEntity<RsData> modifyReview(@PathVariable("review-id") Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext,
                                               @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.modify(memberContext.getId(), reviewId, reviewDto);

        return new ResponseEntity<>(
                new RsData<>(true, "리뷰 수정 완료"),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{review-id}")
    public ResponseEntity<RsData> deleteReview(@PathVariable("review-id") Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext) {
        reviewService.delete(memberContext.getId(), reviewId);

        return new ResponseEntity<>(
                new RsData<>(true, "리뷰 삭제 완료"),
                HttpStatus.OK);
    }

    /**
     * 회원 관련
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/member")
    public ResponseEntity<RsData> getMemberReviews(@AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseReviewDto> reviews = reviewService.getAllMemberReviews(memberContext.getId());

        return new ResponseEntity<>(
                new RsData<>(true, "회원의 모든 리뷰 조회 완료", reviews),
                HttpStatus.OK);
    }

    /**
     * 상점 관련
     */
    @GetMapping("/shop/{shop-id}")
    public ResponseEntity<RsData> getShopReviews(@PathVariable("shop-id") Long shopId) {
        List<ResponseReviewDto> reviews = reviewService.getAllShopReviews(shopId);

        return new ResponseEntity<>(
                new RsData<>(true, "상점의 모든 리뷰 조회 완료", reviews),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/shop/{shop-id}")
    public ResponseEntity<RsData> writeReview(@PathVariable("shop-id") Long shopId,
                                              @AuthenticationPrincipal MemberContext memberContext,
                                              @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.write(memberContext.getId(), shopId, reviewDto);

        return new ResponseEntity<>(
                new RsData<>(true, "상점 리뷰 작성 성공"),
                HttpStatus.OK);
    }
}
