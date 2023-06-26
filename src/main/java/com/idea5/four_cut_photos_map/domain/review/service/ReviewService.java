package com.idea5.four_cut_photos_map.domain.review.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.*;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.mapper.ReviewMapper;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final MemberTitleService memberTitleService;
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;

    private boolean actorCanModify(Member member, Review review) {
        return member.getId() == review.getWriter().getId();
    }

    private boolean actorCanDelete(Member member, Review review) {
        return actorCanModify(member, review);
    }

    @Transactional(readOnly = true)
    public ResponseReviewDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewMapper.toResponseReviewDto(review);
    }

    @Transactional(readOnly = true)
    public List<ResponseShopReviewDto> getAllShopReviews(Long shopId) {
        Shop shop = shopService.findById(shopId);

        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId);   // 최신 작성순
        return reviews.stream()
                .map(review -> ReviewMapper.toResponseShopReviewDto(review))
                .collect(Collectors.toList());
    }

    // 지점 전체 리뷰 조회
    @Transactional(readOnly = true)
    public List<ShopReviewResp> getAllShopReview(Long shopId) {
        Shop shop = shopService.findById(shopId);

        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId);   // 최신 작성순
        return reviews.stream()
                .map(review -> ReviewMapper.toShopReviewResp(
                        review,
                        memberTitleService.getMainMemberTitle(review.getWriter())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseMemberReviewDto> getAllMemberReviews(Long memberId) {
        List<Review> reviews = reviewRepository.findAllByWriterIdOrderByCreateDateDesc(memberId);

        return reviews.stream()
                .map(review -> ReviewMapper.toResponseMemberReviewDto(review))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ResponseShopReviewDto> getTop3ShopReviews(Long shopId) {
        List<Review> reviews = reviewRepository.findTop3ByShopIdOrderByCreateDateDesc(shopId);

        return reviews.stream()
                .map(review -> ReviewMapper.toResponseShopReviewDto(review))
                .collect(Collectors.toList());
    }

    // 리뷰 최신순 3개 조회
    @Transactional(readOnly = true)
    public List<ShopReviewResp> getTop3ShopReview(Long shopId) {
        List<Review> reviews = reviewRepository.findTop3ByShopIdOrderByCreateDateDesc(shopId);

        return reviews.stream()
                .map(review -> ReviewMapper.toShopReviewResp(
                        review,
                        memberTitleService.getMainMemberTitle(review.getWriter())
                ))
                .collect(Collectors.toList());
    }

    public ResponseReviewDto write(Member member, Long shopId, RequestReviewDto reviewDto) {
        Shop shop = shopService.findById(shopId);

        Review savedReview = reviewRepository.save(ReviewMapper.toEntity(member, shop, reviewDto));

        return ReviewMapper.toResponseReviewDto(savedReview);
    }

    public ResponseReviewDto modify(Member member, Long reviewId, RequestReviewDto reviewDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 수정 권한 확인
        if(!actorCanModify(member, review)) {
            throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
        }

        ReviewMapper.update(review, reviewDto);

        return ReviewMapper.toResponseReviewDto(review);
    }

    public Long delete(Member member, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        if(!actorCanDelete(member, review)) {
            throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
        }

        Long shopId = review.getShop().getId();

        reviewRepository.delete(review);

        return shopId;
    }

    // 회원의 리뷰수 조회
    public Long getReviewCntByWriter(Member member) {
        return reviewRepository.countByWriter(member);
    }

    public void deleteByWriterId(Long memberId) {
        reviewRepository.deleteByWriterId(memberId);
    }

    @Transactional(readOnly = true)
    public ShopReviewInfoDto getShopReviewInfo(Long shopId) {
        Shop shop = shopService.findById(shopId);

        int reviewCount = reviewRepository.countByShop(shop);

        double starRatingAvg = 0.0;
        if(reviewCount != 0){
            starRatingAvg = Math.round(reviewRepository.getAverageStarRating(shopId) * 10) / 10.0;
        }

        return new ShopReviewInfoDto(shopId, reviewCount, starRatingAvg);
    }
}
