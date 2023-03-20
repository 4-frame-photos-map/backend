//package com.idea5.four_cut_photos_map.domain.review.integration;
//
//import com.idea5.four_cut_photos_map.domain.base.integration.BaseIntegrationTest;
//import com.idea5.four_cut_photos_map.domain.member.entity.Member;
//import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
//import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
//import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
//import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
//import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
//import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
//import com.idea5.four_cut_photos_map.domain.review.entity.Review;
//import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
//import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
//import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
//import com.idea5.four_cut_photos_map.domain.review.repository.ReviewRepository;
//import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
//import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
//import com.idea5.four_cut_photos_map.global.error.ErrorCode;
//import com.idea5.four_cut_photos_map.security.jwt.JwtService;
//import com.idea5.four_cut_photos_map.security.jwt.dto.response.JwtToken;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//public class ReviewIntegrationTest extends BaseIntegrationTest {
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MemberTitleRepository memberTitleRepository;
//
//    @Autowired
//    private MemberTitleLogRepository memberTitleLogRepository;
//
//    @Autowired
//    private ShopRepository shopRepository;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @AfterEach
//    public void cleanUpDatabase() {
//        databaseCleaner.execute();
//    }
//
//    /**
//     * GET /reviews/{review-id}
//     */
//    @Test
//    @DisplayName("특정 리뷰 조회 요청")
//    void getReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Review review = reviewRepository.save(Review.builder().writer(member).shop(shop).starRating(1).content("소품은 많지만 매장이 청결하지 못합니다.").purity(PurityScore.BAD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());
//
//        Long reviewId = review.getId();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .get("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("리뷰 조회 완료"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.member_info.id").value(review.getWriter().getId()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.member_info.nickname").value(review.getWriter().getNickname()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.shop_info.id").value(review.getShop().getId()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.shop_info.place_name").value(review.getShop().getPlaceName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.star_rating").value(review.getStarRating()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content").value(review.getContent()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.purity").value(String.valueOf(review.getPurity())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.retouch").value(String.valueOf(review.getRetouch())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.item").value(String.valueOf(review.getItem())));
//    }
//
//    /**
//     * GET /reviews/{review-id}
//     * @throws new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
//     */
//    @Test
//    @DisplayName("특정 리뷰 조회 요청 실패")
//    void failedGetReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Long reviewId = 2L;
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .get("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorMessage").value(ErrorCode.REVIEW_NOT_FOUND.getMessage()));
//    }
//
//    /**
//     * PATCH /reviews/{review-id}
//     */
//    @Test
//    @DisplayName("특정 리뷰 수정 요청")
//    void modifyReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Review review = reviewRepository.save(Review.builder().writer(member).shop(shop).starRating(1).content("소품은 많지만 매장이 청결하지 못합니다.").purity(PurityScore.BAD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());
//        Long reviewId = review.getId();
//        RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("가격도 합리적이고 소품이 진짜 다양해서 좋았어요").build();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .patch("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(requestReviewDto))
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("리뷰 수정 완료"));
//    }
//
//    /**
//     * PATCH /reviews/{review-id}
//     * @throws throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
//     */
//    @Test
//    @DisplayName("특정 리뷰 수정 실패")
//    void failedModifyReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member user = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//        Member anotherUser = memberRepository.save(Member.builder().id(2L).nickname("user2").build());
//
//        memberTitleLogRepository.save(new MemberTitleLog(user, memberTitle, true));
//        memberTitleLogRepository.save(new MemberTitleLog(anotherUser, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(user);
//
//        Review review = reviewRepository.save(Review.builder().writer(anotherUser).shop(shop).starRating(1).content("소품은 많지만 매장이 청결하지 못합니다.").purity(PurityScore.BAD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());   // 다른 유저의 리뷰
//        Long reviewId = review.getId();
//        RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("가격도 합리적이고 소품이 진짜 다양해서 좋았어요").build();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .patch("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(requestReviewDto))
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorMessage").value(ErrorCode.WRITER_DOES_NOT_MATCH.getMessage()));
//
//    }
//
//    /**
//     * DELETE /reviews/{review-id}
//     */
//    @Test
//    @DisplayName("특정 리뷰 삭제 요청")
//    void deleteReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Review review = reviewRepository.save(Review.builder().writer(member).shop(shop).starRating(1).content("소품은 많지만 매장이 청결하지 못합니다.").purity(PurityScore.BAD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());
//        Long reviewId = review.getId();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .delete("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("리뷰 삭제 완료"));
//    }
//
//    /**
//     * DELETE /reviews/{review-id}
//     * @throws throw new BusinessException(ErrorCode.WRITER_DOES_NOT_MATCH);
//     */
//    @Test
//    @DisplayName("특정 리뷰 삭제 요청")
//    void failedDeleteReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member user = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//        Member anotherUser = memberRepository.save(Member.builder().id(2L).nickname("user2").build());
//
//        MemberTitleLog memberTitleLog1 = memberTitleLogRepository.save(new MemberTitleLog(user, memberTitle, true));
//        MemberTitleLog memberTitleLog2 = memberTitleLogRepository.save(new MemberTitleLog(anotherUser, memberTitle, true));
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//
//        JwtToken jwtToken = jwtService.generateTokens(user);
//
//        Review review = reviewRepository.save(Review.builder().writer(anotherUser).shop(shop).starRating(1).content("소품은 많지만 매장이 청결하지 못합니다.").purity(PurityScore.BAD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());   // 다른 유저의 Review
//        Long reviewId = review.getId();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .delete("/reviews/" + reviewId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorMessage").value(ErrorCode.WRITER_DOES_NOT_MATCH.getMessage()));
//    }
//
//    /**
//     * GET /reviews/shop/{shop-id}
//     */
//    @Test
//    @DisplayName("상점의 전체 리뷰 조회")
//    void getShopReviews() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member1 = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//        Member member2 = memberRepository.save(Member.builder().id(2L).nickname("user2").build());
//
//        MemberTitleLog memberTitleLog1 = memberTitleLogRepository.save(new MemberTitleLog(member1, memberTitle, true));
//        MemberTitleLog memberTitleLog2 = memberTitleLogRepository.save(new MemberTitleLog(member2, memberTitle, true));
//
//        JwtToken jwtToken = jwtService.generateTokens(member1);
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//        long shopId = shop.getId();
//
//        reviewRepository.save(Review.builder().writer(member1).shop(shop).starRating(3).content("소품이 많고 깔끔하게 잘 나옵니다.").purity(PurityScore.UNSELECTED).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//        reviewRepository.save(Review.builder().writer(member1).shop(shop).starRating(4).content("사진이 깔끔하게 나와 자주 찾습니다.").purity(PurityScore.UNSELECTED).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//        reviewRepository.save(Review.builder().writer(member1).shop(shop).starRating(4).content("매장이 깨끗하고 사진이 잘 나옵니다.").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());
//        reviewRepository.save(Review.builder().writer(member1).shop(shop).starRating(5).content("깔끔해서 자주 방문하고 있습니다.").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .get("/reviews/shop/" + shopId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상점의 모든 리뷰 조회 완료"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.length()").value(4));
//    }
//
//    /**
//     * GET /reviews/shop/{shop-id}
//     */
//    @Test
//    @DisplayName("상점 전체 리뷰 조회 요청 - 데이터 없을 때")
//    void getShopReviews_NoReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Shop shop = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//        long shopId = shop.getId();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .get("/reviews/shop/" + shopId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상점의 모든 리뷰 조회 완료"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.length()").value(0));
//    }
//
//    /**
//     * POST /reviews/shop/{shop-id}
//     */
//    @Test
//    @DisplayName("상점 리뷰 추가 요청")
//    void addShopReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member user = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(user, memberTitle, true));
//
//        JwtToken jwtToken = jwtService.generateTokens(user);
//
//        Shop shop = shopRepository.save(new Shop( "인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//        Long shopId = shop.getId();
//
//        RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("가격도 합리적이고 소품이 진짜 다양해서 좋았어요").build();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .post("/reviews/shop/" + shopId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(requestReviewDto))
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상점 리뷰 작성 성공"));
//
//    }
//
//    /**
//     * POST /reviews/shop/{shop-id}
//     * @throws throw new BusinessException(ErrorCode.SHOP_NOT_FOUND);
//     */
//    @Test
//    @DisplayName("상점 리뷰 추가 요청 실패")
//    void failedAddShopReview() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member user = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(user, memberTitle, true));
//
//        JwtToken jwtToken = jwtService.generateTokens(user);
//
//        Long shopId = 2L;
//
//        RequestReviewDto requestReviewDto = RequestReviewDto.builder().starRating(3).content("가격도 합리적이고 소품이 진짜 다양해서 좋았어요").build();
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .post("/reviews/shop/" + shopId)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(this.objectMapper.writeValueAsString(requestReviewDto))
//        );
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorMessage").value(ErrorCode.SHOP_NOT_FOUND.getMessage()));
//
//    }
//
//    /**
//     * GET /reviews/member
//     */
//    @Test
//    @DisplayName("회원의 전체 리뷰 조회 요청")
//    void getMemberReviews() throws Exception {
//        // given
//        MemberTitle memberTitle = memberTitleRepository.save(MemberTitle.builder().name("뉴비").content("네컷지도 가입").build());
//
//        Member member = memberRepository.save(Member.builder().id(1L).nickname("user1").build());
//
//        MemberTitleLog memberTitleLog = memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
//
//        JwtToken jwtToken = jwtService.generateTokens(member);
//
//        Shop shop1 = shopRepository.save(new Shop("인생네컷 서울숲노가리마트로드점", "서울 성동구 서울숲2길 48"));
//        Shop shop2 = shopRepository.save(new Shop("포토이즘박스 성수점", "서울 성동구 서울숲2길 17-2"));
//
//        reviewRepository.save(Review.builder().writer(member).shop(shop1).starRating(3).content("소품이 많고 깔끔하게 잘 나옵니다.").purity(PurityScore.UNSELECTED).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//        reviewRepository.save(Review.builder().writer(member).shop(shop1).starRating(4).content("사진이 깔끔하게 나와 자주 찾습니다.").purity(PurityScore.UNSELECTED).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//        reviewRepository.save(Review.builder().writer(member).shop(shop2).starRating(4).content("매장이 깨끗하고 사진이 잘 나옵니다.").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.UNSELECTED).build());
//        reviewRepository.save(Review.builder().writer(member).shop(shop2).starRating(5).content("깔끔해서 자주 방문하고 있습니다.").purity(PurityScore.GOOD).retouch(RetouchScore.GOOD).item(ItemScore.GOOD).build());
//
//        // when
//        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
//                .get("/reviews/member")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken()));
//
//        // then
//        resultActions
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원의 모든 리뷰 조회 완료"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.result.length()").value(4));
//    }
//
//}
//>>>>>>> 793717d349d63267be71af3444a133d81a36d9dc
