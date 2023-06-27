package com.idea5.four_cut_photos_map.domain.review.unit.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseMemberReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ResponseShopReviewDto;
import com.idea5.four_cut_photos_map.domain.review.dto.response.ShopReviewInfoDto;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.review.mapper.ReviewMapper;
import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewReadService;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewReadServiceTest {
    @InjectMocks
    private ReviewReadService reviewReadService;
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

                ResponseReviewDto responseReviewDto = reviewReadService.getReviewById(retrieveReviewId);

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
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewReadService.getReviewById(retrieveReviewId));
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

                List<ResponseMemberReviewDto> memberReviews = reviewReadService.getAllMemberReviews(memberId);

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

                List<ResponseMemberReviewDto> memberReviews = reviewReadService.getAllMemberReviews(memberId);

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

                List<ResponseMemberReviewDto> memberReviews = reviewReadService.getAllMemberReviews(memberId);

                // then
                Assertions.assertEquals(memberReviews.size(), 0);
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {

        }
    }

    @Nested
    @DisplayName("지점 전체 리뷰 조회")
    class RetrieveShopReviews {
        private Member writer;
        private Brand brand1;
        private Brand brand2;
        private Brand brand3;
        private Shop shop1;
        private Shop shop2;
        private Shop shop3;
        private Review review1;
        private Review review2;
        private Review review3;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand1 = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            brand2 = Brand.builder().id(2L).brandName("하루필름").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_2.jpg").build();
            brand3 = Brand.builder().id(3L).brandName("포토이즘").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_3.jpg").build();
            shop1 = Shop.builder().id(1L).brand(brand1).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            shop2 = Shop.builder().id(2L).brand(brand2).placeName("하루필름 연트럴파크점").address("서울 마포구 양화로23길 30, 1층 (동교동)").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            shop3 = Shop.builder().id(3L).brand(brand3).placeName("포토이즘박스 광운대점").address("서울 노원구 석계로 95 성북빌딩").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review1 = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop1).starRating(5).content("shop1 작성한 리뷰 내용-1").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();
            review2 = Review.builder().id(2L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop1).starRating(4).content("shop1 작성한 리뷰 내용-2").purity(PurityScore.UNSELECTED).retouch(RetouchScore.UNSELECTED).item(ItemScore.UNSELECTED).build();
            review3 = Review.builder().id(3L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop2).starRating(3).content("shop2 작성한 리뷰 내용-1").purity(PurityScore.BAD).retouch(RetouchScore.BAD).item(ItemScore.BAD).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("shop1 리뷰 조회")
            void retrieveShopReviewsSuccess1() {
                // given
                Long shopId = 1L;
                List<Review> reviews = new ArrayList<>();
                reviews.add(review1);
                reviews.add(review2);

                // when
                when(shopService.findById(shopId)).thenReturn(shop1);
                when(reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId)).thenReturn(reviews);

                List<ResponseShopReviewDto> shopReviews = reviewReadService.getAllShopReviews(shopId);

                // then
                Assertions.assertEquals(shopReviews.size(), reviews.size());

                Assertions.assertEquals(shopReviews.get(0).getReviewInfo().getId(), review1.getId());
                Assertions.assertEquals(shopReviews.get(0).getReviewInfo().getContent(), review1.getContent());
                Assertions.assertEquals(shopReviews.get(0).getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(shopReviews.get(0).getMemberInfo().getNickname(), writer.getNickname());

                Assertions.assertEquals(shopReviews.get(1).getReviewInfo().getId(), review2.getId());
                Assertions.assertEquals(shopReviews.get(1).getReviewInfo().getContent(), review2.getContent());
                Assertions.assertEquals(shopReviews.get(1).getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(shopReviews.get(1).getMemberInfo().getNickname(), writer.getNickname());
            }

            @Test
            @DisplayName("shop2 리뷰 조회")
            void retrieveShopReviewsSuccess2() {
                // given
                Long shopId = 2L;
                List<Review> reviews = new ArrayList<>();
                reviews.add(review3);

                // when
                when(shopService.findById(shopId)).thenReturn(shop2);
                when(reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId)).thenReturn(reviews);

                List<ResponseShopReviewDto> shopReviews = reviewReadService.getAllShopReviews(shopId);

                // then
                Assertions.assertEquals(shopReviews.size(), reviews.size());

                Assertions.assertEquals(shopReviews.get(0).getReviewInfo().getId(), review3.getId());
                Assertions.assertEquals(shopReviews.get(0).getReviewInfo().getContent(), review3.getContent());
                Assertions.assertEquals(shopReviews.get(0).getMemberInfo().getId(), writer.getId());
                Assertions.assertEquals(shopReviews.get(0).getMemberInfo().getNickname(), writer.getNickname());
            }

            @Test
            @DisplayName("shopId 해당하는 리뷰 없음")
            void retrieveShopReviewsSuccess3() {
                // given
                Long shopId = 3L;
                List<Review> reviews = new ArrayList<>();

                // when
                when(shopService.findById(shopId)).thenReturn(shop3);
                when(reviewRepository.findAllByShopIdOrderByCreateDateDesc(shopId)).thenReturn(reviews);

                List<ResponseShopReviewDto> shopReviews = reviewReadService.getAllShopReviews(shopId);

                // then
                Assertions.assertEquals(shopReviews.size(), reviews.size());
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("ShopId 해당하는 Shop 없음")
            void retrieveShopReviewsFail1() {
                // given
                Long shopId = 4L;
                BusinessException exception = new BusinessException(ErrorCode.SHOP_NOT_FOUND);

                // when
                when(shopService.findById(shopId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewReadService.getAllShopReviews(shopId));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("상점의 리뷰 정보 가져오기")
    class GetShopReviewInfo {

        private Brand brand;
        private Shop shop;

        @BeforeEach
        void setUp() {
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("shopId 해당하는 상점 정보 가져오기")
            void getShopReviewInfoSuccess1() {
                // given
                Long shopId = 1L;
                int reviewCount = 10;
                double starRatingAvg = 4.12345678;
                double roundedStarRatingAvg = 4.1;

                // when
                when(shopService.findById(shopId)).thenReturn(shop);
                when(reviewRepository.countByShop(shop)).thenReturn(reviewCount);
                when(reviewRepository.getAverageStarRating(shopId)).thenReturn(starRatingAvg);

                ShopReviewInfoDto shopReviewInfo = reviewReadService.getShopReviewInfo(shopId);

                // then
                Assertions.assertEquals(shopReviewInfo.getShopId(), shopId);
                Assertions.assertEquals(shopReviewInfo.getReviewCnt(), reviewCount);
                Assertions.assertEquals(shopReviewInfo.getStarRatingAvg(), roundedStarRatingAvg);
            }

            @Test
            @DisplayName("상점의 리뷰가 없을 때 별점 평균 처리")
            void getShopReviewInfoSuccess2() {
                // given
                Long shopId = 1L;
                int reviewCount = 0;
                double starRatingAvg = 0.0;

                // when
                when(shopService.findById(shopId)).thenReturn(shop);
                when(reviewRepository.countByShop(shop)).thenReturn(reviewCount);

                ShopReviewInfoDto shopReviewInfo = reviewReadService.getShopReviewInfo(shopId);

                // then
                Assertions.assertEquals(shopReviewInfo.getShopId(), shopId);
                Assertions.assertEquals(shopReviewInfo.getReviewCnt(), reviewCount);
                Assertions.assertEquals(shopReviewInfo.getStarRatingAvg(), starRatingAvg);
            }
        }

        @Nested
        @DisplayName("실패")
        class FailCase {
            @Test
            @DisplayName("shopId 존재하지 않는 경우")
            void getShopReviewInfoFail1() {
                // given
                Long shopId = 1L;
                BusinessException exception = new BusinessException(ErrorCode.SHOP_NOT_FOUND);

                // when
                when(shopService.findById(shopId)).thenThrow(exception);

                // then
                BusinessException resultException = Assertions.assertThrows(exception.getClass(), () -> reviewReadService.getShopReviewInfo(shopId));
                Assertions.assertEquals(resultException.getErrorCode(), exception.getErrorCode());
                Assertions.assertEquals(resultException.getMessage(), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("회원의 리뷰수 조회")
    class GetReviewCntByWriter {
        // 리뷰 추가
        private Member writer;
        private Brand brand1;
        private Brand brand2;
        private Shop shop1;
        private Shop shop2;
        private Review review1;
        private Review review2;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("리뷰 작성자").build();
            brand1 = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            brand2 = Brand.builder().id(2L).brandName("하루필름").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_2.jpg").build();
            shop1 = Shop.builder().id(1L).brand(brand1).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            shop2 = Shop.builder().id(2L).brand(brand2).placeName("하루필름 연트럴파크점").address("서울 마포구 양화로23길 30, 1층 (동교동)").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
            review1 = Review.builder().id(1L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop1).starRating(5).content("shop1 작성한 리뷰 내용").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();
            review2 = Review.builder().id(2L).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop2).starRating(4).content("shop2 작성한 리뷰 내용").purity(PurityScore.UNSELECTED).retouch(RetouchScore.UNSELECTED).item(ItemScore.UNSELECTED).build();
        }

        @Nested
        @DisplayName("성공")
        class SuccessCase {
            @Test
            @DisplayName("회원 정보가진 리뷰 존재")
            void getReviewCntByWriterSuccess1() {
                // given
                Member user = writer;
                Long userReviewCnt = 2L;

                // when
                when(reviewRepository.countByWriter(user)).thenReturn(userReviewCnt);

                Long result = reviewReadService.getReviewCntByWriter(user);

                // then
                Assertions.assertEquals(userReviewCnt, result);
            }

            @Test
            @DisplayName("회원 정보가진 리뷰 존재하지 않음")
            void getReviewCntByWriterSuccess2() {
                // given
                Member user = Member.builder().id(2L).kakaoId(2000L).nickname("현재 접속자").build();
                Long userReviewCnt = 0L;

                // when
                when(reviewRepository.countByWriter(user)).thenReturn(userReviewCnt);

                Long resultCnt = reviewReadService.getReviewCntByWriter(user);

                // then
                Assertions.assertEquals(userReviewCnt, resultCnt);
            }
        }

    }

    @Nested
    @DisplayName("최신 리뷰 3건 가져오기")
    class GetTop3ShopReviews {
        private Member writer;
        private Brand brand;
        private Shop shop;

        @BeforeEach
        void setUp() {
            writer = Member.builder().id(1L).kakaoId(1000L).nickname("user1").build();
            brand = Brand.builder().id(1L).brandName("인생네컷").filePath("https://d18tllc1sxg8cp.cloudfront.net/brand_image/brand_1.jpg").build();
            shop = Shop.builder().id(1L).brand(brand).placeName("인생네컷망리단길점").address("서울 마포구 포은로 109-1").favoriteCnt(0).reviewCnt(0).starRatingAvg(0.0).build();
        }

        List<Review> createReviews(int n) {
            ArrayList<Review> reviews = new ArrayList<>();
            for(int i=1; i<=n; i++) {
                Review review = Review.builder().id((long)i).createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now()).writer(writer).shop(shop).starRating(i).content("리뷰 " + i + "번 내용").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build();
                reviews.add(review);
            }

            return reviews;
        }

        List<Review> getTop3ShopReviews (List<Review> reviews){
            List<Review> top3ShopReviews = new ArrayList<>();

            for(int i= reviews.size()-1; i>=reviews.size()-3 && i>=0; i--) {
                top3ShopReviews.add(reviews.get(i));
            }

            return top3ShopReviews;
        }

        @Nested
        @DisplayName("성공")
        class GetTop3ShopReviewsSuccessCase {
            @Test
            @DisplayName("상점 리뷰 5건 중 최신 리뷰 3건만 조회")
            void getTop3ShopReviewsSuccess1() {
                // given
                Long shopId = 1L;
                int reviewCnt = 5;

                List<Review> shopReviews = createReviews(reviewCnt);
                List<Review> topShopReviews = getTop3ShopReviews(shopReviews);
                List<ResponseShopReviewDto> topShopReviewDtoList = topShopReviews.stream()
                        .map(review -> ReviewMapper.toResponseShopReviewDto(review))
                        .collect(Collectors.toList());

                // when
                when(reviewRepository.findTop3ByShopIdOrderByCreateDateDesc(shopId)).thenReturn(topShopReviews);

                List<ResponseShopReviewDto> resultShopReviewDtoList = reviewReadService.getTop3ShopReviews(shopId);

                // then
                Assertions.assertEquals(resultShopReviewDtoList.size(), 3);
                Assertions.assertEquals(resultShopReviewDtoList.get(0).getReviewInfo().getId(), topShopReviewDtoList.get(0).getReviewInfo().getId());
                Assertions.assertEquals(resultShopReviewDtoList.get(1).getReviewInfo().getId(), topShopReviewDtoList.get(1).getReviewInfo().getId());
                Assertions.assertEquals(resultShopReviewDtoList.get(2).getReviewInfo().getId(), topShopReviewDtoList.get(2).getReviewInfo().getId());
            }

            @Test
            @DisplayName("상점 리뷰 3건 이하일 때 리뷰 조회")
            void getTop3ShopReviewsSuccess2() {
                // given
                Long shopId = 1L;
                int reviewCnt = 2;

                List<Review> shopReviews = createReviews(2);
                List<Review> topShopReviews = getTop3ShopReviews(shopReviews);
                List<ResponseShopReviewDto> topShopReviewDtoList = topShopReviews.stream()
                        .map(review -> ReviewMapper.toResponseShopReviewDto(review))
                        .collect(Collectors.toList());

                // when
                when(reviewRepository.findTop3ByShopIdOrderByCreateDateDesc(shopId)).thenReturn(topShopReviews);

                List<ResponseShopReviewDto> resultShopReviewDtoList = reviewReadService.getTop3ShopReviews(shopId);

                // then
                Assertions.assertEquals(resultShopReviewDtoList.size(), reviewCnt);
                Assertions.assertEquals(resultShopReviewDtoList.get(0).getReviewInfo().getId(), topShopReviewDtoList.get(0).getReviewInfo().getId());
                Assertions.assertEquals(resultShopReviewDtoList.get(1).getReviewInfo().getId(), topShopReviewDtoList.get(1).getReviewInfo().getId());
            }
        }
    }
}
