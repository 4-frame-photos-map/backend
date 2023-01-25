package com.idea5.four_cut_photos_map.review.service;

import com.idea5.four_cut_photos_map.review.dto.request.WriteReviewDTO;
import com.idea5.four_cut_photos_map.review.entity.Review;
import com.idea5.four_cut_photos_map.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> findAllByShopId(Long shopId) {
        return reviewRepository.findAllByShopId(shopId);
    }

    //public Review writeReview(Long shopId, WriteReviewDTO reviewDTO) {
    //    return null;
    //}
}
