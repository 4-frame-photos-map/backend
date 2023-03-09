package com.idea5.four_cut_photos_map.domain.member.service;

import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoTokenResp;
import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
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
        // DB 에 저장된 member 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getNickname()).isEqualTo("딸기");
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token");

        // redis 에 저장된 nickname, kakaoAccessToken 검증
        String nicknameKey = "member:" + member.getId() + ":nickname";
        String kakaoAtkKey = "member:" + member.getId() + ":kakao_access_token";
        assertThat(redisDao.getValues(nicknameKey)).isEqualTo("딸기");
        assertThat(redisDao.getValues(kakaoAtkKey)).isEqualTo("kakao_access_token");
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
        // 1. DB 의 kakaoRefreshToken 값만 수정됬는지 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(1);

        Member member = members.get(0);
        assertThat(member.getId()).isEqualTo(1);
        assertThat(member.getKakaoId()).isEqualTo(1111);
        assertThat(member.getNickname()).isEqualTo("딸기");
        assertThat(member.getKakaoRefreshToken()).isEqualTo("kakao_refresh_token2");

        // 2. redis 의 kakaoAccessToken 값만 수정됬는지 검증
        String nicknameKey = "member:" + member.getId() + ":nickname";
        String kakaoAtkKey = "member:" + member.getId() + ":kakao_access_token";
        assertThat(redisDao.getValues(nicknameKey)).isEqualTo("딸기");
        assertThat(redisDao.getValues(kakaoAtkKey)).isEqualTo("kakao_access_token2");
    }
}