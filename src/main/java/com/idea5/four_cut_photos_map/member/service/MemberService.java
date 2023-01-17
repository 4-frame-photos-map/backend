package com.idea5.four_cut_photos_map.member.service;

import com.idea5.four_cut_photos_map.member.CachedMemberParam;
import com.idea5.four_cut_photos_map.member.dto.response.KakaoUserInfoDto;
import com.idea5.four_cut_photos_map.member.entity.Member;
import com.idea5.four_cut_photos_map.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    // TODO: 신규가입자인 경우에만 save 가 일어나는데 @Transactional 을 여기에 붙어도 되는가?, DTO 로 반환하는게 맞을까?
    // 회원 가져오기
    @Transactional
    public Member getMember(KakaoUserInfoDto kakaoUserInfoDto) {
        // Unique 한 값인 kakaoId 로 조회
        Member member = memberRepository.findByKakaoId(kakaoUserInfoDto.getId()).orElse(null);
        // 신규 사용자인 경우 회원가입
        if(member == null) {
            member = join(kakaoUserInfoDto);
        }
        return member;
    }

    // 회원가입
    public Member join(KakaoUserInfoDto kakaoUserInfoDto) {
        Member member = KakaoUserInfoDto.toEntity(kakaoUserInfoDto);
        return memberRepository.save(member);
    }

    // 회원의 AccessToken 가져오기(토큰이 없으면 발급)
    @Transactional
    public String getAccessToken(Member member) {
        log.info("회원의 jwt Access Token 가져오기");
        // 1. DB에서 AccessToken 조회
        String accessToken = member.getAccessToken();
        // 2. 만료시, 토큰 새로 발급
        if (StringUtils.hasLength(accessToken) == false) {
            // 지금으로부터 100년간의 유효기간을 가지는 토큰을 생성, DB에 토큰 저장
            accessToken = jwtProvider.generateAccessToken(member.getId(), member.getAuthorities());
            member.updateAccessToken(accessToken);
            memberRepository.save(member);  // TODO: 생략하면 저장 안됨(변경감지X)
        }
        return accessToken;
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }
}
