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

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;
    private final MemberService memberService;

    public List<Review> findAllByShopId(Long shopId) {

        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId);   // 최신 작성순

        if (reviews.isEmpty()){
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return reviews;
    }

    public List<ResponseReviewDto> searchAllReviewsInTheStore(Long shopId) {
        Shop shop = shopService.findShopById(shopId);

        List<Review> reviews = findAllByShopId(shopId);

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

        if (verifyExistedReview(memberId, shopId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .writer(writer)
                .shop(shop)
                .starRating(reviewDto.getStarRating())
                .content(reviewDto.getContent())
                .purity(reviewDto.getPurity() == null ? PurityScore.UNSELECTED : PurityScore.valueOf(reviewDto.getPurity()))
                .retouch(reviewDto.getRetouch() == null ? RetouchScore.UNSELECTED : RetouchScore.valueOf(reviewDto.getRetouch()))
                .item(reviewDto.getItem() == null ? ItemScore.UNSELECTED : ItemScore.valueOf(reviewDto.getItem()))
                .build();

        reviewRepository.save(review);

        return ResponseReviewDto.from(review, writer, shop);
    }

    public ResponseReviewDto modify(WriteReviewDto reviewDto, Long shopId, Long memberId) {
        Shop shop = shopService.findShopById(shopId);

        Member user = memberService.findById(memberId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Review review = reviewRepository.findByWriterIdAndShopId(memberId, shopId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 입력된 데이터로 Review Entity 수정
        review.setStarRating(reviewDto.getStarRating());
        review.setContent(reviewDto.getContent());
        review.setPurity(reviewDto.getPurity() == null ? PurityScore.UNSELECTED : PurityScore.valueOf(reviewDto.getPurity()));
        review.setRetouch(reviewDto.getRetouch() == null ? RetouchScore.UNSELECTED : RetouchScore.valueOf(reviewDto.getRetouch()));
        review.setItem(reviewDto.getItem() == null ? ItemScore.UNSELECTED : ItemScore.valueOf(reviewDto.getItem()));

        reviewRepository.save(review);  // 병합

        return ResponseReviewDto.from(review, user, shop);
    }

    private boolean verifyExistedReview(Long memberId, Long shopId) {
        return reviewRepository.findByWriterIdAndShopId(memberId, shopId).orElse(null) != null;
    }
}
