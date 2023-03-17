package com.idea5.four_cut_photos_map.security.jwt;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.AccessToken;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;

/**
 * redis 를 이용하는 jwt 서비스(토큰 발급/재발급, 블랙리스트 검사)
 * //TODO: 이렇게 분리하는 것이 맞는지는 모르겠음 더 고민해봐야할 것 같음
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    // accessToken, refreshToken 발급
    @Transactional
    public JwtToken generateTokens(Member member) {
        String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getAuthorities());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId(), member.getAuthorities());
        // refreshToken redis 에 저장
        redisDao.setValues(
                RedisDao.getRtkKey(member.getId()),
                refreshToken,
                Duration.ofMillis(60 * 60 * 24 * 30L));

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // accessToken 재발급
    public AccessToken reissueAccessToken(String refreshToken, Long memberId, Collection<? extends GrantedAuthority> authorities) {
        // 1. redis 에서 memberId(key)로 refreshToken 조회
        String redisRefreshToken = redisDao.getValues(RedisDao.getRtkKey(memberId));
        // 2. redis 에 저장된 refreshToken 과 요청 헤더로 전달된 refreshToken 값이 일치하는지 확인
        if(!refreshToken.equals(redisRefreshToken)) {
            throw new RuntimeException("refreshToken 불일치");
        }
        // 3. accessToken 재발급
        String newAccessToken = jwtProvider.generateAccessToken(memberId, authorities);
        return AccessToken.builder()
                .accessToken(newAccessToken)
                .build();
    }

    // accessToken 이 블랙리스트로 등록되었는지 검증
    public Boolean isBlackList(String accessToken) {
        String isLogout = redisDao.getValues(RedisDao.getBlackListAtkKey(accessToken));
        return isLogout == null ? false : true;
    }
}
