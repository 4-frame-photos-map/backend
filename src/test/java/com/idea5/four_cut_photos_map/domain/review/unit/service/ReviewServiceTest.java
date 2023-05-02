package com.idea5.four_cut_photos_map.domain.review.unit.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;


    @Nested
    @DisplayName("단일 리뷰 검색")
    class RetrieveSingleReview {
        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("해당 id를 가진 리뷰 존재")
            void retrieveSingleReviewSuccess1() {
                //given
                Long reviewId = 1L;
                String content = "dd";
                Brand brand = Brand.builder().brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
                Member writer = Member.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).kakaoId(1000L).nickname("user1").build();
                Shop shop = Shop.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
                Review review = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(5).content(content).purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();

                // when
                when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

                ResponseReviewDto responseReviewDto = reviewService.getReviewById(reviewId);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getId(), reviewId);
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), content);

                Assertions.assertEquals(responseReviewDto.getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getPlaceName(), shop.getPlaceName());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getBrand(), brand.getBrandName());

                Assertions.assertEquals(responseReviewDto.getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(responseReviewDto.getMemberInfo().getNickname(), writer.getNickname());
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("해당 id의 리뷰 존재하지 않음")
            void retrieveSingleReviewFail1() {
                // given
                Long reviewId = 1L;

                // when
                when(reviewRepository.findById(reviewId)).thenThrow(new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

                BusinessException e = Assertions.assertThrows(BusinessException.class, () -> reviewService.getReviewById(reviewId));

                // then
                Assertions.assertEquals(ErrorCode.REVIEW_NOT_FOUND.getMessage(), e.getMessage());
            }
        }
    }

}
