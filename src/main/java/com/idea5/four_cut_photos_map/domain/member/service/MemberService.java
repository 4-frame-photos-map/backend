package com.idea5.four_cut_photos_map.domain.member.service;

import com.idea5.four_cut_photos_map.domain.member.dto.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberInfoResp;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.AccessToken;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    // 회원 가져오기
    @Transactional
    public Member getMember(KakaoUserInfoParam kakaoUserInfoParam) {
        // Unique 한 값인 kakaoId 로 조회
        Member member = memberRepository.findByKakaoId(kakaoUserInfoParam.getId()).orElse(null);
        // 신규 사용자인 경우 회원가입
        if(member == null) {
            member = join(kakaoUserInfoParam);
        }
        return member;
    }

    // 회원가입
    public Member join(KakaoUserInfoParam kakaoUserInfoParam) {
        Member member = KakaoUserInfoParam.toEntity(kakaoUserInfoParam);
        return memberRepository.save(member);
    }

    // accessToken, refreshToken 발급
    @Transactional
    public Token generateTokens(Member member) {
        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getAuthorities());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId(), member.getAuthorities());
        // refreshToken redis 에 저장(key, value, 유효시간)
        redisDao.setValues(member.getId().toString(), refreshToken, Duration.ofMillis(60 * 60 * 24 * 30L));

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    // accessToken 재발급
    public AccessToken reissueAccessToken(String refreshToken, Long memberId, Collection<? extends GrantedAuthority> authorities) {
        return jwtProvider.reissueAccessToken(refreshToken, memberId, authorities);
    }

    // 회원 id 로 기본 정보 조회
    public MemberInfoResp getMemberInfo(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        return MemberInfoResp.toDto(member);
    }

    // 서비스 로그아웃(accessToken 무효화)
    public void logout(String accessToken) {
        // 1. 회원의 refreshToken 이 있으면 삭제
        Long memberId = jwtProvider.getId(accessToken);
        if(redisDao.hasKey(memberId.toString())) {
            redisDao.deleteValues(memberId.toString());
        }
        // 2. redis 에 해당 accessToken 블랙리스트로 등록
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisDao.setValues(accessToken, "logout", Duration.ofMillis(expiration));
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id, String accessToken) {
        // 1. 회원의 refreshToken 이 있으면 삭제
        if (redisDao.hasKey(id.toString())) {
            redisDao.deleteValues(id.toString());
        }
        // 2. redis 에 해당 accessToken 블랙리스트로 등록
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisDao.setValues(accessToken, "withdrawl", Duration.ofMillis(expiration));
        // 3. DB 에서 회원 삭제
        memberRepository.deleteById(id);
    }
}
