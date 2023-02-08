package com.idea5.four_cut_photos_map.domain.review.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.review.dto.request.WriteReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;
    private final MemberService memberService;

    public List<ResponseReviewDto> findAllByShopId(Long shopId) {
        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId);   // 최신 작성순

        if (reviews.isEmpty()){
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return reviews.stream()
                .map(review -> ResponseReviewDto.from(review))
                .collect(Collectors.toList());
    }

    public ResponseReviewDto write(WriteReviewDto reviewDto, Long shopId, Long memberId) {

        Member writer = memberService.findById(memberId);
        if (writer == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Shop shop = shopService.findShopById(shopId);
        if (shop == null) {
            throw new BusinessException(ErrorCode.SHOP_NOT_FOUND);
        }

        String purity = reviewDto.getPurity();
        if (purity == null || purity.equals("")) {
            purity = "UNSELECTED";
        }

        String retouch = reviewDto.getRetouch();
        if (retouch == null || retouch.equals("")) {
            retouch = "UNSELECTED";
        }

        String item = reviewDto.getItem();
        if (item == null || item.equals("")) {
            item = "UNSELECTED";
        }

        Review review = Review.builder()
                .writer(writer)
                .shop(shop)
                .starRating(reviewDto.getStarRating())
                .content(reviewDto.getContent())
                .purity(PurityScore.valueOf(purity))
                .retouch(RetouchScore.valueOf(retouch))
                .item(ItemScore.valueOf(item))
                .build();

        reviewRepository.save(review);

        return ResponseReviewDto.from(review);
    }

}
