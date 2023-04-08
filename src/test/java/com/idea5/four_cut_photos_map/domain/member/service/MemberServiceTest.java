package com.idea5.four_cut_photos_map.domain.member.service;

import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoTokenResp;
import com.idea5.four_cut_photos_map.domain.auth.dto.param.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.dto.request.MemberUpdateReq;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import com.idea5.four_cut_photos_map.job.CollectJob;
import com.idea5.four_cut_photos_map.security.jwt.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    JwtService jwtService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberTitleRepository memberTitleRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    MemberTitleLogRepository memberTitleLogRepository;

    @Autowired
    RedisDao redisDao;

    @Autowired
    CollectJob collectJob;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @AfterEach
    void clear() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("새로운 회원이 로그인 요청시 회원가입 처리(DB에 저장), Redis 에 kakaoAccessToken 저장")
    void t1() {
        // given

        // when
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));

        // then
        // 1. DB 저장된 member 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token");

        // 2. Redis 저장된 kakaoAccessToken 검증
        assertThat(redisDao.getValues(RedisDao.getKakaoAtkKey(member.getId()))).isEqualTo("kakao_access_token");
    }

    @Test
    @DisplayName("기존 회원이 로그인 요청시 DB 의 kakaoRefreshToken 수정, Redis 의 kakaoAccessToken 수정")
    void t2() {
        // given
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));

        // when
        memberService.login(
                new KakaoUserInfoParam(1111L, "수박"),
                new KakaoTokenResp("bearer", "kakao_access_token2", 60, "kakao_refresh_token2", 86400));

        // then
        // 1. DB kakaoRefreshToken 값만 수정됬는지 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token2");

        // 2. Redis kakaoAccessToken 값이 수정됬는지 검증
        assertThat(redisDao.getValues(RedisDao.getKakaoAtkKey(member.getId()))).isEqualTo("kakao_access_token2");
    }

    @Test
    @DisplayName("회원 탈퇴시 DB 에서 삭제, Redis 의 kakaoAccessToken, refreshToken 삭제")
    void t3() {
        // given
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        Member member = memberRepository.findById(1L).orElse(null);

        // when
        memberService.deleteMember(member.getId());

        // then
        assertAll(
                // 1. DB Member 삭제
                () -> assertThat(memberRepository.count()).isEqualTo(0),
                // 2. Redis kakaoAccessToken, jwtRefreshToken 삭제
                () -> assertThat(redisDao.getValues(RedisDao.getKakaoAtkKey(member.getId()))).isNull(),
                () -> assertThat(redisDao.getValues(RedisDao.getRtkKey(member.getId()))).isNull()
        );
    }

    @Test
    @DisplayName("회원 닉네임 수정시 DB 의 Member nickname, Redis 의 nickname 수정")
    void t4() {
        // given
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        Member member = memberRepository.findById(1L).orElse(null);
        // when
        memberService.updateNickname(member.getId(), new MemberUpdateReq("수박"));

        // then
        // 1. DB Member nickname 수정 검증
        Member updateMember = memberRepository.findById(member.getId()).orElse(null);
        assertThat(updateMember.getNickname()).isEqualTo("수박");
    }

    @Test
    @DisplayName("중복된 닉네임으로 닉네임 수정 불가")
    void t5() {
        // given
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        Member member = memberRepository.findById(1L).orElse(null);
        // when, then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                memberService.updateNickname(member.getId(), new MemberUpdateReq(member.getNickname()))
        );
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.DUPLICATE_MEMBER_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 대표 칭호를 찜 첫 걸음으로 설정")
    void t6() {
        // given
        MemberTitle memberTitle1 = memberTitleRepository.save(new MemberTitle("뉴비", "회원가입"));
        MemberTitle memberTitle2 = memberTitleRepository.save(new MemberTitle("리뷰 첫 걸음", "첫번째 리뷰 작성"));
        MemberTitle memberTitle3 = memberTitleRepository.save(new MemberTitle("리뷰 홀릭", "리뷰 3개 이상 작성"));
        MemberTitle memberTitle4 = memberTitleRepository.save(new MemberTitle("찜 첫 걸음", "첫번째 찜 추가"));
        MemberTitle memberTitle5 = memberTitleRepository.save(new MemberTitle("찜 홀릭", "찜 3개 이상 추가"));

        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        Member member = memberRepository.findById(1L).orElse(null);
        Shop shop = shopRepository.save(new Shop("인생네컷 성수점", "서울시", 0,0,0.0));
        favoriteRepository.save(new Favorite(member, shop));

        collectJob.add();

        // when
        memberService.updateMainMemberTitle(member, memberTitle4.getId());

        // then
        // 뉴비 -> 대표 칭호 해제, 찜 첫 걸음 대표 칭호 설정
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findByMember(member);
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        MemberTitleLog memberTitleLog2 = memberTitleLogs.get(1);

        assertThat(memberTitleLog1.getMemberTitle().getId()).isEqualTo(memberTitle1.getId());
        assertThat(memberTitleLog1.getIsMain()).isFalse();

        assertThat(memberTitleLog2.getMemberTitle().getId()).isEqualTo(memberTitle4.getId());
        assertThat(memberTitleLog2.getIsMain()).isTrue();
    }

    @Test
    @DisplayName("회원 로그아웃 시, Redis 의 kakaoAccessToken, refreshToken 삭제")
    void t7() {
        // given
        memberService.login(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        Member member = memberRepository.findById(1L).orElse(null);

        // when
        memberService.logout(member.getId());

        // then
        assertAll(
                // 1. Redis kakaoAccessToken, jwtRefreshToken 삭제
                () -> assertThat(redisDao.getValues(RedisDao.getRtkKey(member.getId()))).isNull(),
                () -> assertThat(redisDao.getValues(RedisDao.getKakaoAtkKey(member.getId()))).isNull()
        );
    }
}