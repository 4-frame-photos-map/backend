package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
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

    /**
     * 리뷰 단건 조회
     */
    @GetMapping("/{review-id}")
    public ResponseEntity<ResponseReviewDto> getReview(@PathVariable("review-id") Long reviewId) {
        ResponseReviewDto responseReviewDto = reviewService.getReviewById(reviewId);

        return ResponseEntity.ok(responseReviewDto);
    }

    /**
     * 특정 리뷰 수정
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{review-id}")
    public ResponseEntity<String> modifyReview(@PathVariable("review-id") Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext,
                                               @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.modify(memberContext.getMember(), reviewId, reviewDto);

        return ResponseEntity.ok("리뷰 수정 완료");
    }

    /**
     * 특정 리뷰 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{review-id}")
    public ResponseEntity<String> deleteReview(@PathVariable("review-id") Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext) {
        reviewService.delete(memberContext.getMember(), reviewId);

        return ResponseEntity.ok("리뷰 삭제 완료");
    }

    /**
     * 회원 전체 리뷰 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/member")
    public ResponseEntity<List<ResponseMemberReviewDto>> getMemberReviews(@AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseMemberReviewDto> reviews = reviewService.getAllMemberReviews(memberContext.getId());

        return ResponseEntity.ok(reviews);
    }

    /**
     * 지점 전체 리뷰 조회
     */
    @GetMapping("/shop/{shop-id}")
    public ResponseEntity<List<ResponseShopReviewDto>> getShopReviews(@PathVariable("shop-id") Long shopId) {
        List<ResponseShopReviewDto> reviews = reviewService.getAllShopReviews(shopId);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 상점 리뷰 작성
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/shop/{shop-id}")
    public ResponseEntity<String> writeReview(@PathVariable("shop-id") Long shopId,
                                              @AuthenticationPrincipal MemberContext memberContext,
                                              @Valid @RequestBody RequestReviewDto reviewDto) {
        ResponseReviewDto responseReviewDto = reviewService.write(memberContext.getMember(), shopId, reviewDto);

        return ResponseEntity.ok("상점 리뷰 작성 성공");
    }
}
