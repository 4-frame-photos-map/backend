package com.idea5.four_cut_photos_map.domain.review.controller;

import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/reviews")
public class MemberReviewController {
    private final ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<RsData> getMemberReviews(@AuthenticationPrincipal MemberContext memberContext) {
        List<ResponseReviewDto> reviews = reviewService.getAllMemberReviews(memberContext.getId());

        RsData<List<ResponseReviewDto>> body = new RsData<>(
                true,
                "회원의 모든 리뷰 조회 완료",
                reviews
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
