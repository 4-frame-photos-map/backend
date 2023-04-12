package com.idea5.four_cut_photos_map.domain.review.mapper;

import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ShopDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;

public class ReviewMapper {

    /**
     * 작성자, 지점 정보가 담긴 리뷰 DTO 반환
     */
    public static ResponseReviewDto toResponseReviewDto(Review review) {
        ReviewDto reviewDto = ReviewDto.builder()
                .id(review.getId())
                .createDate(review.getCreateDate())
                .modifyDate(review.getModifyDate())
                .starRating(review.getStarRating())
                .content(review.getContent())
                .purity(review.getPurity())
                .retouch(review.getRetouch())
                .item(review.getItem())
                .build();

        MemberDto memberDto = MemberDto.builder()
                .id(review.getWriter().getId())
                .nickname(review.getWriter().getNickname())
                .build();

        ShopDto shopDto = ShopDto.builder()
                .id(review.getShop().getId())
                .brand(review.getShop().getBrand().getBrandName())
                .placeName(review.getShop().getPlaceName())
                .build();

        return ResponseReviewDto.builder()
                .reviewInfo(reviewDto)
                .memberInfo(memberDto)
                .shopInfo(shopDto)
                .build();
    }

    /**
     * 지점 정보가 담긴 리뷰 DTO 반환
     */
    public static ResponseShopReviewDto toResponseShopReviewDto(Review review) {
        ReviewDto reviewDto = ReviewDto.builder()
                .id(review.getId())
                .createDate(review.getCreateDate())
                .modifyDate(review.getModifyDate())
                .starRating(review.getStarRating())
                .content(review.getContent())
                .purity(review.getPurity())
                .retouch(review.getRetouch())
                .item(review.getItem())
                .build();

        MemberDto memberDTO = MemberDto.builder()
                .id(review.getWriter().getId())
                .nickname(review.getWriter().getNickname())
                .build();

        return ResponseShopReviewDto.builder()
                .reviewInfo(reviewDto)
                .memberInfo(memberDTO)
                .build();
    }
}
