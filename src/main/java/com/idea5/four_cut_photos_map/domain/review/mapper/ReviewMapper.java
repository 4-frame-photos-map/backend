package com.idea5.four_cut_photos_map.domain.review.mapper;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ShopDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;

public class ReviewMapper {

    /**
     * Review -> ReviewDto
     */
    private static ReviewDto toReviewDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .createDate(review.getCreateDate())
                .modifyDate(review.getModifyDate())
                .starRating(review.getStarRating())
                .content(review.getContent())
                .purity(review.getPurity())
                .retouch(review.getRetouch())
                .item(review.getItem())
                .build();
    }

    /**
     * Review.writer -> MemberDto
     */
    private static MemberDto toMemberDto(Member writer) {
        return MemberDto.builder()
                .id(writer.getId())
                .nickname(writer.getNickname())
                .build();
    }

    /**
     * Review.shop -> ShopDto
     */
    private static ShopDto toShopDto(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .brand(shop.getBrand().getBrandName())
                .placeName(shop.getPlaceName())
                .build();
    }


    /**
     * 작성자, 지점 정보가 담긴 리뷰 DTO 반환
     */
    public static ResponseReviewDto toResponseReviewDto(Review review) {
        return ResponseReviewDto.builder()
                .reviewInfo(toReviewDto(review))
                .memberInfo(toMemberDto(review.getWriter()))
                .shopInfo(toShopDto(review.getShop()))
                .build();
    }


    /**
     * 지점 정보가 담긴 리뷰 DTO 반환
     */
    public static ResponseShopReviewDto toResponseShopReviewDto(Review review) {
        return ResponseShopReviewDto.builder()
                .reviewInfo(toReviewDto(review))
                .memberInfo(toMemberDto(review.getWriter()))
                .build();
    }

    /**
     * 상점 정보가 담긴 리뷰 DTO 반환
     */
    public static ResponseMemberReviewDto toResponseMemberReviewDto(Review review) {
        return ResponseMemberReviewDto.builder()
                .reviewInfo(toReviewDto(review))
                .shopInfo(toShopDto(review.getShop()))
                .build();
    }
}