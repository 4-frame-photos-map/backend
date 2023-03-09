package com.idea5.four_cut_photos_map.domain.member.service;

import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoTokenResp;
import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
import com.idea5.four_cut_photos_map.security.jwt.JwtService;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.JwtToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    RedisDao redisDao;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @AfterEach
    void clear() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("새로운 회원이 로그인 요청시 회원가입 처리(DB에 저장), Redis 에 nickname, kakaoAccessToken 저장")
    void t1() {
        // given

        // when
        memberService.getMember(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));

        // then
        // 1. DB 저장된 member 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getNickname()).isEqualTo("딸기");
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token");

        // 2. Redis 저장된 nickname, kakaoAccessToken 검증
        assertThat(redisDao.getValues("member:" + member.getId() + ":nickname")).isEqualTo("딸기");
        assertThat(redisDao.getValues("member:" + member.getId() + ":kakao_access_token"))
                .isEqualTo("kakao_access_token");
    }

    @Test
    @DisplayName("기존 회원이 로그인 요청시 DB 의 kakaoRefreshToken 수정, Redis 의 kakaoAccessToken 수정")
    void t2() {
        // given

        // when
        memberService.getMember(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));

        memberService.getMember(
                new KakaoUserInfoParam(1111L, "수박"),
                new KakaoTokenResp("bearer", "kakao_access_token2", 60, "kakao_refresh_token2", 86400));

        // then
        // 1. DB kakaoRefreshToken 값만 수정됬는지 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getNickname()).isEqualTo("딸기");
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token2");

        // 2. Redis kakaoAccessToken 값만 수정됬는지 검증
        assertThat(redisDao.getValues("member:" + member.getId() + ":nickname")).isEqualTo("딸기");
        assertThat(redisDao.getValues("member:" + member.getId() + ":kakao_access_token"))
                .isEqualTo("kakao_access_token2");
    }

    @Test
    @DisplayName("회원 탈퇴시 DB 에서 삭제, Redis 의 refreshToken 삭제, accessToken 블랙리스트로 등록")
    void t3() {
        // given
        Member member = memberService.getMember(
                new KakaoUserInfoParam(1111L, "딸기"),
                new KakaoTokenResp("bearer", "kakao_access_token", 60, "kakao_refresh_token", 86400));
        JwtToken jwtToken = jwtService.generateTokens(member);
        String key3 = "member:" + member.getId() + ":jwt_refresh_token";
        assertThat(redisDao.getValues(key3)).isEqualTo(jwtToken.getRefreshToken());

        // when
        memberService.deleteMember(member.getId(), jwtToken.getAccessToken());

        // then
        // 1. DB Member 삭제
        assertThat(memberRepository.count()).isEqualTo(0);

        // 2. Redis jwtAccessToken 블랙리스트 등록, jwtRefreshToken 삭제
        assertThat(redisDao.getValues("jwt_black_list:" + jwtToken.getAccessToken())).isEqualTo("withdrawl");
        assertThat(redisDao.getValues("member:" + member.getId() + ":jwt_refresh_token")).isNull();
    }
}