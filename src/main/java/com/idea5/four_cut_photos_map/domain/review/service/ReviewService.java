package com.idea5.four_cut_photos_map.domain.review.service;

import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> findAllByShopId(Long shopId) {

        return reviewRepository.findAllByShopId(shopId);
//        List<Review> reviews = reviewRepository.findAllByShopId(shopId);
//
//        if (reviews.isEmpty())
//            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
//
//        List<ResponseReview> responseReviews = new ArrayList<>();
//
//        for (Review review : reviews) {
//            responseReviews.add(ResponseReview.from(review));
//        }
//
//        return responseReviews;
    }

    //public Review writeReview(Long shopId, WriteReviewDTO reviewDTO) {
    //    return null;
    //}
}
