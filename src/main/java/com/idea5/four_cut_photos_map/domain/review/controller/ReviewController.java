package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewInfoDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewResp;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ShopService shopService;

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

        // 추후 배치 등 이용해서 상점 정보 갱신
        ShopReviewInfoDto shopReviewInfo = reviewService.getShopReviewInfo(responseReviewDto.getShopInfo().getId());
        shopService.updateReviewInfo(shopReviewInfo);

        return ResponseEntity.ok("리뷰 수정 완료");
    }

    /**
     * 특정 리뷰 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{review-id}")
    public ResponseEntity<String> deleteReview(@PathVariable("review-id") Long reviewId,
                                               @AuthenticationPrincipal MemberContext memberContext) {
        Long shopId = reviewService.delete(memberContext.getMember(), reviewId);

        // 추후 배치 등 이용해서 상점 정보 갱신
        ShopReviewInfoDto shopReviewInfo = reviewService.getShopReviewInfo(shopId);
        shopService.updateReviewInfo(shopReviewInfo);

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
    public ResponseEntity<List<ShopReviewResp>> getShopReviews(@PathVariable("shop-id") Long shopId) {
        List<ShopReviewResp> reviews = reviewService.getAllShopReview(shopId);

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

        // 추후 배치 등 이용해서 상점 정보 갱신
        ShopReviewInfoDto shopReviewInfo = reviewService.getShopReviewInfo(responseReviewDto.getShopInfo().getId());
        shopService.updateReviewInfo(shopReviewInfo);

        return ResponseEntity.ok("상점 리뷰 작성 성공");
    }
}
