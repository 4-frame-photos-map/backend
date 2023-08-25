package com.idea5.four_cut_photos_map.domain.review.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.mapper.ReviewMapper;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewWriteService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;

    private void authorizeReviewWriter(Member member, Review review) {
        if(!(member.getId() == review.getWriter().getId()))
            throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
    }

    public ResponseReviewDto write(Member member, Long shopId, RequestReviewDto reviewDto) {
        Shop shop = shopService.findById(shopId);

        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(member, shop, reviewDto));

        return ReviewMapper.toResponseReviewDto(savedReview);
    }

    public ResponseReviewDto modify(Member member, Long reviewId, RequestReviewDto reviewDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        authorizeReviewWriter(member, review);

        ReviewMapper.update(review, reviewDto);

        return ReviewMapper.toResponseReviewDto(review);
    }

    public Long delete(Member member, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        Long shopId = review.getShop().getId();

        authorizeReviewWriter(member, review);

        reviewRepository.delete(review);
        return shopId;
    }

    public void deleteByWriterId(Long memberId) {
        reviewRepository.deleteByWriterId(memberId);
    }
}
