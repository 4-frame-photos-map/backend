package com.idea5.four_cut_photos_map.security.jwt;

import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;

import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.ACCESS_TOKEN;
import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.REFRESH_TOKEN;

/**
 * JWT 토큰 생성, 검증 관여
 * @See<a href="https://brunch.co.kr/@jinyoungchoi95/1">jwt</>
 * @See<a href="https://annajin.tistory.com/217">jwt 토큰 검증 예외</>
 * @See<a href="https://yeon-blog.tistory.com/3">Claims 객체</>
 * @See<a href="https://velog.io/@jkijki12/Jwt-Refresh-Token-%EC%A0%81%EC%9A%A9%EA%B8%B0">refresh token 발급</>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    // TODO: 테스트를 위해 유효기간을 30초로 세팅
    @Value("${jwt.atk.expiration}")
    private long accessTokenValidationSecond; // accessToken 유효기간(30분)

    @Value("${jwt.rtk.expiration}")
    private long refreshTokenValidationSecond;    // accessToken 유효기간(1달)

    private final SecretKey jwtSecretKey;   // 비밀키
    private final RedisDao redisDao;

    private SecretKey getSecretKey() {
        return jwtSecretKey;
    }

    /**
     * JWT Access Token 발급
     * @param memberId 회원 id
     * @param authorities 회원 Authority 리스트
     * @param tokenValid 토큰 유효기간
     * @return jwt access token
     */
    public String generateToken(Long memberId, Collection<? extends GrantedAuthority> authorities, String tokenType, Long tokenValid) {
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setIssuer("four_cut_photos_map")   // 토큰 발급자
                .setIssuedAt(now)   // 토큰 발급 시간
                .setExpiration(new Date(now.getTime() + 1000L * tokenValid));   // 토큰 만료 시간
        claims.put("token_type", tokenType);    // 토큰 타입
        // 회원 기반 정보
        claims.put("id", memberId);
        claims.put("authorities", authorities);

        return Jwts.builder()
                .setClaims(claims)  // Custom Claims 정보(맨 위에 적지않으면 아래 값이 덮어씌워져 누락됨!)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // HS512, 비밀키로 서명
                .compact(); // 토큰 생성
    }

    // accessToken 발급
    public String generateAccessToken(Long memberId, Collection<? extends GrantedAuthority> authorities) {
        log.info("accessToken 발급");
        return generateToken(memberId, authorities, ACCESS_TOKEN.getName(), accessTokenValidationSecond);
    }

    // refreshToken 발급
    public String generateRefreshToken(Long memberId, Collection<? extends GrantedAuthority> authorities) {
        log.info("refreshToken 발급");
        return generateToken(memberId, authorities, REFRESH_TOKEN.getName(), refreshTokenValidationSecond);
    }

    // JWT Access Token 검증
    public boolean verify(String accessToken) {
        Jwts.parserBuilder()
                .setSigningKey(getSecretKey())  // 비밀키
                .build()
                .parseClaimsJws(accessToken);   // 파싱 및 검증(실패시 에러)
        return true;
    }

    // accessToken 으로부터 Claim 정보 얻기
    private Claims parseClaims(String accessToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        log.info(claims.toString());
        return claims;
    }

    // Claims 에서 id 조회
    public Long getId(String accessToken) {
        Claims claims = parseClaims(accessToken);
        // java.lang.Integer cannot be cast to java.lang.Long 오류해결
        return ((Number) claims.get("id")).longValue();
    }

    // Claims 에서 id 조회
    public String getTokenType(String accessToken) {
        Claims claims = parseClaims(accessToken);
        // java.lang.Integer cannot be cast to java.lang.Long 오류해결
        return claims.get("token_type").toString();
    }

    // accessToken 재발급
    public AccessToken reissueAccessToken(String refreshToken, Long memberId, Collection<? extends GrantedAuthority> authorities) {
        // 1. redis 에서 memberId(key)로 refreshToken 조회
        String redisRefreshToken = redisDao.getValues(memberId.toString());
        // 2. redis 에 저장된 refreshToken 과 요청 헤더로 전달된 refreshToken 값이 일치하는지 확인
        if(!refreshToken.equals(redisRefreshToken)) {
            throw new RuntimeException("refreshToken 불일치");
        }
        // 3. accessToken 재발급
        String newAccessToken = generateAccessToken(memberId, authorities);
        return AccessToken.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
