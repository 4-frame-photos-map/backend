package com.idea5.four_cut_photos_map.domain.review.mapper;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.MemberResp;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.entity.ShopDto;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewResp;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;

public class ReviewMapper {
    private static RequestReviewDto setDefaultScore(RequestReviewDto dto) {
        if(dto.getPurity() == null) dto.setPurity("UNSELECTED");
        if(dto.getRetouch() == null) dto.setRetouch("UNSELECTED");
        if(dto.getItem() == null) dto.setItem("UNSELECTED");

        return dto;
    }
    public static Review toEntity(Member writer, Shop shop, RequestReviewDto dto) {
        dto = setDefaultScore(dto);

        return Review.builder()
                .writer(writer)
                .shop(shop)
                .starRating(dto.getStarRating())
                .content(dto.getContent())
                .purity(PurityScore.valueOf(dto.getPurity()))
                .retouch(RetouchScore.valueOf(dto.getRetouch()))
                .item(ItemScore.valueOf(dto.getItem()))
                .build();
    }

    public static Review update(Review review, RequestReviewDto dto) {
        dto = setDefaultScore(dto);

        return review.update(dto);
    }

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

    private static MemberResp toMemberResp(Member writer, String mainMemberTitle) {
        return MemberResp.builder()
                .id(writer.getId())
                .nickname(writer.getNickname())
                .mainMemberTitle(mainMemberTitle)
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

    // 리뷰, 회원 정보가 담긴 지점 리뷰 정보 반환
    public static ShopReviewResp toShopReviewResp(Review review, String mainMemberTitle) {
        return ShopReviewResp.builder()
                .reviewInfo(toReviewDto(review))
                .memberInfo(toMemberResp(review.getWriter(), mainMemberTitle))
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
