package com.idea5.four_cut_photos_map.domain.review.unit.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewWriteService;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewWriteServiceTest {
    @InjectMocks
    private ReviewWriteService reviewWriteService;
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ShopService shopService;

    @Nested
    @DisplayName("상점 리뷰 작성")
    class WriteReviewShop {
        private Member writer;
        private Brand brand;
        private Shop shop;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("shopId 가진 지점에 review 추가")
            void writeReviewShopSuccess1() {
                // given
                Long shopId = 1L;
                RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("새로 지점에 추가하는 리뷰 내용").purity("GOOD").retouch("GOOD").item("GOOD").build();
                Review review = Review.builder()
                        .id(1L)
                        .createDate(LocalDateTime.now())
                        .modifyDate(LocalDateTime.now())
                        .writer(writer)
                        .shop(shop)
                        .starRating(requestReviewDto.getStarRating())
                        .content(requestReviewDto.getContent())
                        .purity(PurityScore.valueOf(requestReviewDto.getPurity()))
                        .retouch(RetouchScore.valueOf(requestReviewDto.getRetouch()))
                        .item(ItemScore.valueOf(requestReviewDto.getItem()))
                        .build();

                // when
                when(shopService.findById(shopId)).thenReturn(shop);
                when(reviewRepository.save(any(Review.class))).thenReturn(review);

                ResponseReviewDto responseReviewDto = reviewWriteService.write(writer, shopId, requestReviewDto);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getId(), review.getId());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), review.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), review.getContent());

            }

            @Test
            @DisplayName("shopId 가진 지점에 purity, retouch, item null인 review 추가")
            void retrieveShopReviewsSuccess2() {
                // given
                Long shopId = 1L;
                RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("새로 지점에 추가하는 리뷰 내용").build();
                Review review = Review.builder()
                        .id(1L)
                        .createDate(LocalDateTime.now())
                        .modifyDate(LocalDateTime.now())
                        .writer(writer)
                        .shop(shop)
                        .starRating(requestReviewDto.getStarRating())
                        .content(requestReviewDto.getContent())
                        .purity(PurityScore.UNSELECTED)
                        .retouch(RetouchScore.UNSELECTED)
                        .item(ItemScore.UNSELECTED)
                        .build();

                // when
                when(shopService.findById(shopId)).thenReturn(shop);
                when(reviewRepository.save(any(Review.class))).thenReturn(review);

                ResponseReviewDto responseReviewDto = reviewWriteService.write(writer, shopId, requestReviewDto);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getId(), review.getId());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), review.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), review.getContent());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getPurity(), review.getPurity());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getRetouch(), review.getRetouch());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getItem(), review.getItem());

                Assertions.assertEquals(responseReviewDto.getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getPlaceName(), shop.getPlaceName());
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("ShopId 존재하지 않는 지점")
            void retrieveShopReviewsFail1() {
                // given
                Long shopId = 2L;
                RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("새로 지점에 추가하는 리뷰 내용").purity("GOOD").retouch("GOOD").item("GOOD").build();
                BusinessException exception = new BusinessException(ErrorCode.SHOP_NOT_FOUND);

                // when
                when(shopService.findById(shopId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewWriteService.write(writer, shopId, requestReviewDto));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
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
            review = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(5).content("리뷰 내용").purity(PurityScore.BAD).retouch(RetouchScore.BAD).item(ItemScore.BAD).build();
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

                ResponseReviewDto responseReviewDto = reviewWriteService.modify(user, modifyReviewId, modifyReviewDto);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), modifyReviewDto.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), modifyReviewDto.getContent());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getPurity(), PurityScore.valueOf(modifyReviewDto.getPurity()));
                Assertions.assertEquals(String.valueOf(responseReviewDto.getReviewInfo().getRetouch()), modifyReviewDto.getRetouch());
                Assertions.assertEquals(String.valueOf(responseReviewDto.getReviewInfo().getItem()), modifyReviewDto.getItem());
            }

            @Test
            @DisplayName("purity, retouch, item를 null로 전송")
            void modifyReviewSuccess2() {
                // given
                Long modifyReviewId = 1L;
                Member user = Member.builder().id(1L).build();
                RequestReviewDto modifyReviewDto = RequestReviewDto.builder().starRating(3).content("수정 후 리뷰 내용").build();

                // when
                when(reviewRepository.findById(modifyReviewId)).thenReturn(Optional.of(review));

                ResponseReviewDto responseReviewDto = reviewWriteService.modify(user, modifyReviewId, modifyReviewDto);

                // then
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getId(), modifyReviewId);
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getStarRating(), modifyReviewDto.getStarRating());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getContent(), modifyReviewDto.getContent());
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getPurity(), PurityScore.UNSELECTED);
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getRetouch(), RetouchScore.UNSELECTED);
                Assertions.assertEquals(responseReviewDto.getReviewInfo().getItem(), ItemScore.UNSELECTED);

                Assertions.assertEquals(responseReviewDto.getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(responseReviewDto.getMemberInfo().getNickname(), writer.getNickname());

                Assertions.assertEquals(responseReviewDto.getShopInfo().getId(), shop.getId());
                Assertions.assertEquals(responseReviewDto.getShopInfo().getPlaceName(), shop.getPlaceName());
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
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewWriteService.modify(user, modifyReviewId, modifyReviewDto));
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
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewWriteService.modify(user, modifyReviewId, modifyReviewDto));
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
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewWriteService.delete(user, deleteReviewId));
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
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewWriteService.delete(user, modifyReviewId));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

}
