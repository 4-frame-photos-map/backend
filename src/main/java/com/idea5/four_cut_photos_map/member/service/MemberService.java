package com.idea5.four_cut_photos_map.member.service;

import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.member.dto.response.KakaoUserInfoDto;
import com.idea5.four_cut_photos_map.member.entity.Member;
import com.idea5.four_cut_photos_map.member.repository.MemberRepository;
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

    // accessToken, refreshToken 발급
    @Transactional
    public Token generateTokens(Member member) {
        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getAuthorities());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId(), member.getAuthorities());
        // TODO: DB에 refresh 를 저장하는 것으로 수정예정
        member.updateAccessToken(accessToken);
        memberRepository.save(member);  // 생략하면 저장 안됨(변경 감지X)
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
}
