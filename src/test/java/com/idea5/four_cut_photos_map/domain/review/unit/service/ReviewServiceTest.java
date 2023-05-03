package com.idea5.four_cut_photos_map.domain.review.unit.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewService;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ShopService shopService;

    @Nested
    @DisplayName("단일 리뷰 검색")
    class RetrieveSingleReview {
        private Member writer;
        private Brand brand;
        private Shop shop;
        private Review review;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(5).content("리뷰 내용").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("해당 id를 가진 리뷰 존재")
            void retrieveSingleReviewSuccess1() {
                //given
                Long retrieveReviewId = 1L;

                // when
                when(reviewRepository.findById(retrieveReviewId)).thenReturn(Optional.of(review));

                ResponseReviewDto responseReviewDto = reviewService.getReviewById(retrieveReviewId);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getId(), review.getId());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), review.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), review.getContent());

                Assertions.assertEquals(responseReviewDto.getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(responseReviewDto.getMemberInfo().getNickname(), writer.getNickname());

                Assertions.assertEquals(responseReviewDto.getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getBrand(), brand.getBrandName());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getPlaceName(), shop.getPlaceName());
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("해당 id의 리뷰 존재하지 않음")
            void retrieveSingleReviewFail1() {
                // given
                Long retrieveReviewId = 2L;
                BusinessException exception = new BusinessException(ErrorCode.REVIEW_NOT_FOUND);

                // when
                when(reviewRepository.findById(retrieveReviewId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewService.getReviewById(retrieveReviewId));
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("특정 리뷰 수정")
    class ModifyReview {
        private Member writer;
        private Brand brand;
        private Shop shop;
        private Review review;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(5).content("리뷰 내용").purity(PurityScore.UNSELECTED).retouch(RetouchScore.UNSELECTED).item(ItemScore.UNSELECTED).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("해당 id 가진 리뷰 수정")
            void modifyReviewSuccess1() {
                // given
                Long modifyReviewId = 1L;
                Member user = Member.builder().id(1L).build();
                RequestReviewDto modifyReviewDto = RequestReviewDto.builder().starRating(3).content("수정 후 리뷰 내용").purity("GOOD").retouch("GOOD").item("GOOD").build();

                // when
                when(reviewRepository.findById(modifyReviewId)).thenReturn(Optional.of(review));

                ResponseReviewDto responseReviewDto = reviewService.modify(user, modifyReviewId, modifyReviewDto);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), modifyReviewDto.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), modifyReviewDto.getContent());
                Assertions.assertEquals(String.valueOf(responseReviewDto.getReviewInfo().getPurity()), modifyReviewDto.getPurity());
                Assertions.assertEquals(String.valueOf(responseReviewDto.getReviewInfo().getRetouch()), modifyReviewDto.getRetouch());
                Assertions.assertEquals(String.valueOf(responseReviewDto.getReviewInfo().getItem()), modifyReviewDto.getItem());
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("해당 id 가진 리뷰 존재하지 않음")
            void modifyReviewFail1() {
                // given
                Long modifyReviewId = 2L;
                Member user = Member.builder().id(1L).build();
                RequestReviewDto modifyReviewDto = RequestReviewDto.builder().starRating(3).content("수정 후 리뷰 내용").purity("GOOD").retouch("GOOD").item("GOOD").build();
                BusinessException exception = new BusinessException(ErrorCode.REVIEW_NOT_FOUND);

                // when
                when(reviewRepository.findById(modifyReviewId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewService.modify(user, modifyReviewId, modifyReviewDto));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }

            @Test
            @DisplayName("사용자와 리뷰 작성자 불일치")
            void modifyReviewFail2() {
                // given
                Long modifyReviewId = 1L;
                Member user = Member.builder().id(2L).build();
                RequestReviewDto modifyReviewDto = RequestReviewDto.builder().starRating(3).content("수정 후 리뷰 내용").purity("GOOD").retouch("GOOD").item("GOOD").build();
                BusinessException exception = new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);

                // when
                when(reviewRepository.findById(modifyReviewId)).thenReturn(Optional.of(review));

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewService.modify(user, modifyReviewId, modifyReviewDto));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("특정 리뷰 삭제")
    class DeleteReview {
        private Member writer;
        private Brand brand;
        private Shop shop;
        private Review review;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(5).content("리뷰 내용").purity(PurityScore.UNSELECTED).retouch(RetouchScore.UNSELECTED).item(ItemScore.UNSELECTED).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {

        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("해당 id 가진 리뷰 존재하지 않음")
            void modifyReviewFail1() {
                // given
                Long deleteReviewId = 2L;
                Member user = Member.builder().id(1L).build();
                BusinessException exception = new BusinessException(ErrorCode.REVIEW_NOT_FOUND);

                // when
                when(reviewRepository.findById(deleteReviewId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewService.delete(user, deleteReviewId));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }

            @Test
            @DisplayName("사용자와 리뷰 작성자 불일치")
            void modifyReviewFail2() {
                // given
                Long modifyReviewId = 1L;
                Member user = Member.builder().id(2L).build();
                BusinessException exception = new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);

                // when
                when(reviewRepository.findById(modifyReviewId)).thenReturn(Optional.of(review));

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewService.delete(user, modifyReviewId));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("회원 전체 리뷰 조회")
    class RetrieveMemberReviews {
        private Member writer1;
        private Member writer2;
        private Brand brand;

        private Shop shop;
        private Review review1;
        private Review review2;
        private Review review3;

        @BeforeEach
        void setUp() {
            writer1 = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            writer2 = Member.builder().id(2L).kakaoId(2000L).nickname("user2").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review1 = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer1).shop(shop).starRating(5).content("user1 작성한 리뷰 내용-1").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();
            review2 = Review.builder().id(2L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer1).shop(shop).starRating(4).content("user1 작성한 리뷰 내용-2").purity(PurityScore.UNSELECTED).retouch(RetouchScore.UNSELECTED).item(ItemScore.UNSELECTED).build();
            review3 = Review.builder().id(3L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer2).shop(shop).starRating(3).content("user2 작성한 리뷰 내용-1").purity(PurityScore.BAD).retouch(RetouchScore.BAD).item(ItemScore.BAD).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("user1 리뷰 조회")
            void retrieveMemberReviewSuccess1() {
                // given
                Long memberId = 1L;
                List<Review> reviews = new ArrayList<>();
                reviews.add(review1);
                reviews.add(review2);

                // when
                when(reviewRepository.findAllByWriterIdOrderByCreateDateDesc(memberId)).thenReturn(reviews);

                List<ResponseMemberReviewDto> memberReviews = reviewService.getAllMemberReviews(memberId);

                // then
                Assertions.assertEquals(memberReviews.size(), 2);

                Assertions.assertEquals(memberReviews.get(0).getReviewInfo().getId(), review1.getId());
                Assertions.assertEquals(memberReviews.get(0).getReviewInfo().getContent(), review1.getContent());
                Assertions.assertEquals(memberReviews.get(0).getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(memberReviews.get(0).getShopInfo().getPlaceName(), shop.getPlaceName());

                Assertions.assertEquals(memberReviews.get(1).getReviewInfo().getId(), review2.getId());
                Assertions.assertEquals(memberReviews.get(1).getReviewInfo().getContent(), review2.getContent());
                Assertions.assertEquals(memberReviews.get(1).getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(memberReviews.get(1).getShopInfo().getPlaceName(), shop.getPlaceName());

            }

            @Test
            @DisplayName("user2 리뷰 조회")
            void retrieveMemberReviewSuccess2() {
                Long memberId = 2L;
                List<Review> reviews = new ArrayList<>();
                reviews.add(review3);

                // when
                when(reviewRepository.findAllByWriterIdOrderByCreateDateDesc(memberId)).thenReturn(reviews);

                List<ResponseMemberReviewDto> memberReviews = reviewService.getAllMemberReviews(memberId);

                // then
                Assertions.assertEquals(memberReviews.size(), 1);

                Assertions.assertEquals(memberReviews.get(0).getReviewInfo().getId(), review3.getId());
                Assertions.assertEquals(memberReviews.get(0).getReviewInfo().getContent(), review3.getContent());

                Assertions.assertEquals(memberReviews.get(0).getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(memberReviews.get(0).getShopInfo().getPlaceName(), shop.getPlaceName());
            }

            @Test
            @DisplayName("user1 회원 리뷰가 존재하지 않음")
            void retrieveMemberReviewSuccess3() {
                // given
                Long memberId = 3L;
                List<Review> reviews = new ArrayList<>();

                // when
                when(reviewRepository.findAllByWriterIdOrderByCreateDateDesc(memberId)).thenReturn(reviews);

                List<ResponseMemberReviewDto> memberReviews = reviewService.getAllMemberReviews(memberId);

                // then
                Assertions.assertEquals(memberReviews.size(), 0);
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {

        }
    }

}
