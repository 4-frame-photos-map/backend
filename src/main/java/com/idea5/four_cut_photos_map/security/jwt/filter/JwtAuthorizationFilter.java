package com.idea5.four_cut_photos_map.security.jwt.filter;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.security.jwt.JwtService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.ACCESS_TOKEN;
import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.REFRESH_TOKEN;

/**
 * JWT 인증처리 필터
 * - OncePerRequestFilter: 한 요청에 대해 딱 한 번만 실행하는 필터(한 요청에 대해서 redirect 로 인해 불필요하게 인증필터를 n번 이상 거치는 다중인증처리 상황을 해결하기 위함)
 * @See <a href="https://dev-racoon.tistory.com/34">OncePerRequestFilter</a>
 * @See <a href="https://velog.io/@shinmj1207/Spring-Spring-Security-JWT-%EB%A1%9C%EA%B7%B8%EC%9D%B8">Spring Security + JWT 로그인</a>
 * @See <a href="https://alkhwa-113.tistory.com/entry/TIL-JWT-%EC%99%80-%EB%B3%B4%EC%95%88-CORS">JWT 토큰 무효화 참고1</a>
 * @See <a href="https://mellowp-dev.tistory.com/8">JWT 토큰 무효화 참고2</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;
    private final MemberService memberService;
    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.atk.header}")
    private String tokenHeader;

    // 토큰 유효성 검증 후 인증(로그인)처리
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthorizationFilter doFilterInternal()");
        String token = getJwtToken(request);
        // 1. 토큰이 유효한지 검증
        if(StringUtils.hasText(token) && jwtProvider.verify(token)) {
            Long memberId = jwtProvider.getId(token);
            String tokenType = jwtProvider.getTokenType(token);
            String requestURI = request.getRequestURI();
            // 2. 올바른 토큰 타입(ATK, RTK)으로 요청했는지 검증(아래 2가지 예외)
            // 2-1. accessToken 재발급 요청에 accessToken 을 담아 요청한 경우
            // 2-2. accessToken 재발급 외의 요청에 refreshToken 을 담아 요청한 경우
            if(tokenType.equals(ACCESS_TOKEN.getName()) && requestURI.equals("/member/refresh")
            || tokenType.equals(REFRESH_TOKEN.getName()) && !requestURI.equals("/member/refresh")) {
                throw new JwtException("유효하지 않은 토큰입니다.");
            }
            // 3. 해당 accessToken 이 블랙리스트로 redis 에 등록되었는지 검증
            if(tokenType.equals(ACCESS_TOKEN.getName()) && jwtService.isBlackList(token)) {
                throw new JwtException("유효하지 않은 토큰입니다.");
            }
            // TODO: 매 요청마다 DB 조회하면 성능 문제(jwt 쓰는 이유가 없음) -> Redis 캐시로 해결
            Member member = memberService.findById(memberId);
//            CachedMemberParam cachedMember = memberService.findCachedById(memberId);
//            log.info(cachedMember.getId().toString());
//            log.info(cachedMember.getNickname());
            // 2. 2차 체크(해당 엑세스 토큰이 화이트 리스트에 포함되는지 검증) -> 탈취된 토큰 무효화
            if(member != null) {
                log.info("JwtAuthorizationFilter 인증처리");
                forceAuthentication(member);
            }
        }
        filterChain.doFilter(request, response);
    }

    // request Authorization header 의 jwt token 값 꺼내기
    private String getJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }

    // 강제 로그인 처리
    private void forceAuthentication(Member member) {
        log.info(member.getId().toString());
        // Member 를 기반으로 User 를 상속한 MemberContext 객체 생성
        MemberContext memberContext = new MemberContext(member);
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        null,
                        member.getAuthorities()
                );
        // 이후 컨트롤러 단에서 MemberContext 객체 사용O
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
