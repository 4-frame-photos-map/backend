//package com.idea5.four_cut_photos_map.security.jwt.filter;
//
//import com.idea5.four_cut_photos_map.domain.member.entity.Member;
//import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
//import com.idea5.four_cut_photos_map.security.jwt.JwtService;
//import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
//import io.jsonwebtoken.JwtException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.ACCESS_TOKEN;
//import static com.idea5.four_cut_photos_map.security.jwt.dto.TokenType.REFRESH_TOKEN;
//
///**
// * JWT 인증처리 필터
// * - OncePerRequestFilter: 한 요청에 대해 딱 한 번만 실행하는 필터(한 요청에 대해서 redirect 로 인해 불필요하게 인증필터를 n번 이상 거치는 다중인증처리 상황을 해결하기 위함)
// * @See <a href="https://dev-racoon.tistory.com/34">OncePerRequestFilter</a>
// * @See <a href="https://velog.io/@shinmj1207/Spring-Spring-Security-JWT-%EB%A1%9C%EA%B7%B8%EC%9D%B8">Spring Security + JWT 로그인</a>
// * @See <a href="https://alkhwa-113.tistory.com/entry/TIL-JWT-%EC%99%80-%EB%B3%B4%EC%95%88-CORS">JWT 토큰 무효화 참고1</a>
// * @See <a href="https://mellowp-dev.tistory.com/8">JWT 토큰 무효화 참고2</a>
// * @See <a href="https://flyburi.com/584">인증 관련 클래스</a>
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//    private final JwtProvider jwtProvider;
//    private final JwtService jwtService;
//    private final String BEARER_TOKEN_PREFIX = "Bearer ";
//
//    @Value("${jwt.atk.header}")
//    private String tokenHeader;
//
//    @Value("${jwt.atk.reissue-uri}")
//    private String atkReissueUri;
//
//    // 토큰 유효성 검증 후 인증(로그인)처리
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("JwtAuthorizationFilter doFilterInternal()");
//        String token = getJwtToken(request);
//        // TODO : 모든 요청에서 필터를 거치기 때문에 인증이 필요없는 API 에서 유효하지않은 accessToken 을 사용하면 오류나는 문제있음
//        // 1. 토큰이 유효한지 검증
//        if(StringUtils.hasText(token) && jwtProvider.verify(token)) {
//            Long memberId = jwtProvider.getId(token);
//            String tokenType = jwtProvider.getTokenType(token);
//            String requestURI = request.getRequestURI();
//            // 2. 올바른 토큰 타입(ATK, RTK)으로 요청했는지 검증(아래 2가지 예외)
//            // 2-1. accessToken 재발급 요청에 accessToken 을 담아 요청한 경우
//            // 2-2. accessToken 재발급 외의 요청에 refreshToken 을 담아 요청한 경우
//            log.info("requestURI=" + requestURI);
//            // refreshToken 으로 인증이 되어버리는 문제 해결
//            if(tokenType.equals(REFRESH_TOKEN.getName()) && !requestURI.equals(atkReissueUri)) {
//                throw new JwtException("유효하지 않은 토큰입니다.");
//            }
////            if(tokenType.equals(ACCESS_TOKEN.getName()) && requestURI.equals(atkReissueUri)
////            || tokenType.equals(REFRESH_TOKEN.getName()) && !requestURI.equals(atkReissueUri)) {
////                throw new JwtException("유효하지 않은 토큰입니다.");
////            }
//            // TODO: accessToken 블랙리스트 검사 없애기
//            // 3. 해당 accessToken 이 블랙리스트로 redis 에 등록되었는지 검증
//            if(tokenType.equals(ACCESS_TOKEN.getName()) && jwtService.isBlackList(token)) {
//                throw new JwtException("유효하지 않은 토큰입니다.");
//            }
//
////            StopWatch stopWatch = new StopWatch();
////            stopWatch.start();
//
//            // 4. jwt 에서 id 를 얻어 Member 객체 생성
//            Member member = Member.builder()
//                    .id(memberId)
//                    .build();
////            Member member = memberService.findById(memberId);
//
////            stopWatch.stop();
////            log.info(stopWatch.prettyPrint());
////            log.info(String.valueOf(stopWatch.getTotalTimeSeconds()));
//
//            // 5. 2차 체크(해당 엑세스 토큰이 화이트 리스트에 포함되는지 검증) -> 탈취된 토큰 무효화
//            if(member != null) {
//                log.info("---Before forceAuthentication()---");
//                forceAuthentication(member);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    // request Authorization header 의 jwt token 값 꺼내기
//    private String getJwtToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(tokenHeader);
//        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
//            return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
//        }
//        return null;
//    }
//
//    // Spring Security 에 유저의 인증 정보 등록(컨트롤러 단에서 @AuthenticationPrincipal 로 인증 객체를 얻기 위함)
//    private void forceAuthentication(Member member) {
//        log.info(member.getId().toString());
//        // 1. Member 를 기반으로 User 를 상속한 MemberContext 객체 생성
//        MemberContext memberContext = new MemberContext(member);
//        // 2. Authentication 객체 생성
//        UsernamePasswordAuthenticationToken authentication =
//                UsernamePasswordAuthenticationToken.authenticated(
//                        memberContext,
//                        null,
//                        member.getAuthorities()
//                );
//        // 3. Authentication 을 SecurityContext 에 담기
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(authentication);
//        // 4. SecurityContext 를 SecurityContextHolder 에 담기
//        SecurityContextHolder.setContext(context);
//    }
//}
