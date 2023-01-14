package com.idea5.four_cut_photos_map.security.jwt;

import com.idea5.four_cut_photos_map.global.util.Util;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 토큰 생성, 검증 관여
 * @See<a href="https://brunch.co.kr/@jinyoungchoi95/1">jwt</>
 * @See<a href="https://annajin.tistory.com/217">jwt 토큰 검증 예외</>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey jwtSecretKey;   // 비밀키
    private long ACCESS_TOKEN_VALIDATION_SECOND = 60 * 60 * 24 * 365 * 100L;    // accessToken 유효기간(100년)

    private SecretKey getSecretKey() {
        return jwtSecretKey;
    }

    /**
     * JWT Access Token 발급
     * @param claims jwt claim 에 들어갈 회원정보를 담은 map 객체
     * @return jwt access token
     */
    public String generateAccessToken(Map<String, Object> claims) {
        log.info("accessToken 발급");
        // TODO : LocalDateTime 직렬화 문제, LocalDateTime -> String 변환
        claims.put("createDate", claims.get("createDate").toString());
        claims.put("modifyDate", claims.get("modifyDate").toString());

        return Jwts.builder()
                .setIssuedAt(new Date())    // 토큰 발급 시간
                .setExpiration(new Date(new Date().getTime() + 1000L * ACCESS_TOKEN_VALIDATION_SECOND)) // 토큰 만료 시간
                .setClaims(claims)          // Custom Claims 정보
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // HS512, 비밀키로 서명
                .compact();                                         // 토큰 생성
    }

    // JWT Access Token 검증
    public boolean verify(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())  // 비밀키
                    .build()
                    .parseClaimsJws(accessToken);   // 파싱 및 검증(실패시 에러)
            return true;
        } catch (SignatureException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.info("유효하지 않은 구성의 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // TODO: 수정 예정
    // accessToken 으로부터 Claim 정보 얻기
    public Map<String, Object> getClaims(String accessToken) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("body", String.class);

        return Util.json.toMap(body);
    }
}
