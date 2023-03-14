package com.idea5.four_cut_photos_map.domain.review.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShopService shopService;
    private final MemberService memberService;

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

        return ResponseReviewDto.from(review);
    }

    @Transactional(readOnly = true)
    public List<ResponseReviewDto> getAllShopReviews(Long shopId) {
        Shop shop = shopService.findById(shopId);

        List<Review> reviews = reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId);   // 최신 작성순

        return reviews.stream()
                .map(review -> ResponseReviewDto.from(review))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseReviewDto> getAllMemberReviews(Long memberId) {
        List<Review> reviews = reviewRepository.findAllByWriterIdOrderByCreateDateDesc(memberId);

        return reviews.stream()
                .map(review -> ResponseReviewDto.from(review))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseReviewDto> getTop3ShopReviews(Long shopId) {
        List<Review> reviews = reviewRepository.findTop3ByShopIdOrderByCreateDateDesc(shopId);

        return reviews.stream()
                .map(review -> ResponseReviewDto.from(review))
                .collect(Collectors.toList());
    }

    public ResponseReviewDto write(Long memberId, Long shopId, RequestReviewDto reviewDto) {
        Member user = memberService.findById(memberId);

        Shop shop = shopService.findById(shopId);

        Review review = Review.builder()
                .writer(user)
                .shop(shop)
                .starRating(reviewDto.getStarRating())
                .content(reviewDto.getContent())
                .purity(reviewDto.getPurity() == null ? PurityScore.UNSELECTED : PurityScore.valueOf(reviewDto.getPurity()))
                .retouch(reviewDto.getRetouch() == null ? RetouchScore.UNSELECTED : RetouchScore.valueOf(reviewDto.getRetouch()))
                .item(reviewDto.getItem() == null ? ItemScore.UNSELECTED : ItemScore.valueOf(reviewDto.getItem()))
                .build();

        reviewRepository.save(review);

        return ResponseReviewDto.from(review, user, shop);
    }

    public ResponseReviewDto modify(Long memberId, Long reviewId, RequestReviewDto reviewDto) {
        Member user = memberService.findById(memberId);

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 수정 권한 확인
        if(!actorCanModify(user, review)) {
            throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
        }

        // Review Entity 수정
        review.setStarRating(reviewDto.getStarRating());
        review.setContent(reviewDto.getContent());
        review.setPurity(reviewDto.getPurity() == null ? PurityScore.UNSELECTED : PurityScore.valueOf(reviewDto.getPurity()));
        review.setRetouch(reviewDto.getRetouch() == null ? RetouchScore.UNSELECTED : RetouchScore.valueOf(reviewDto.getRetouch()));
        review.setItem(reviewDto.getItem() == null ? ItemScore.UNSELECTED : ItemScore.valueOf(reviewDto.getItem()));

        reviewRepository.save(review);  // 병합

        return ResponseReviewDto.from(review);
    }

    public void delete(Long memberId, Long reviewId) {
        Member user = memberService.findById(memberId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        if(!actorCanDelete(user, review)) {
            throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
        }

        reviewRepository.delete(review);
    }
}
