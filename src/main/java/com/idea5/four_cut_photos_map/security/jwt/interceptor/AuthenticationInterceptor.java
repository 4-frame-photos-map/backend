package com.idea5.four_cut_photos_map.security.jwt.interceptor;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import com.idea5.four_cut_photos_map.security.jwt.dto.TokenType;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 인가 인터셉터
 * @See <a href="https://mslilsunshine.tistory.com/170">Interceptor 활용한 인가처리 참고</a>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtProvider jwtProvider;
    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.atk.header}")
    private String tokenHeader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("---AuthenticationInterceptor preHandle()---");
        String requestURI = request.getRequestURI();
        log.info("requestURI=" + requestURI);
        String token = getJwtToken(request);
        // 1. 토큰이 유효한지 검증
        jwtProvider.verify(token);
        // 2. refreshToken 사용 예외
        String tokenType = jwtProvider.getTokenType(token);
        if(tokenType.equals(TokenType.REFRESH_TOKEN.getName()))
            throw new JwtException("");
        // 3. jwt 에서 id 를 얻어 Member 객체 생성
        Long memberId = jwtProvider.getId(token);
        Member member = Member.builder()
                .id(memberId)
                .build();
        // 4. Spring Security 에 유저 인증 정보 등록
        forceAuthentication(member);

        return true;
    }

    // request Authorization header 의 jwt token 값 꺼내기
    private String getJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }

    // Spring Security 에 유저의 인증 정보 등록(컨트롤러 단에서 @AuthenticationPrincipal 로 인증 객체를 얻기 위함)
    private void forceAuthentication(Member member) {
        log.info("---forceAuthentication()---");
        log.info(member.getId().toString());
        // 1. Member 를 기반으로 User 를 상속한 MemberContext 객체 생성
        MemberContext memberContext = new MemberContext(member);
        // 2. Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        null,
                        member.getAuthorities()
                );
        // 3. Authentication 을 SecurityContext 에 담기
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        // 4. SecurityContext 를 SecurityContextHolder 에 담기
        SecurityContextHolder.setContext(context);
    }
}
