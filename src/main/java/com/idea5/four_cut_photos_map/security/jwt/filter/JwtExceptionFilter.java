package com.idea5.four_cut_photos_map.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.global.error.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.idea5.four_cut_photos_map.security.jwt.dto.ErrorCode.*;

/**
 * JWT AuthorizationFilter 예외처리 필터
 * - 필터 단에서 발생한 예외는 @ControllerAdvice 로 처리할 수 없기 때문에 추가함
 * 1) 만료된 JWT 토큰(프론트에게 토큰 재발급 요청이 필요함을 알리는 목적으로 구분)
 * 2) 유효하지 않은 JWT 토큰(잘못된 서명, 올바르지 않은 JWT 구조 등 여러 예외 통합 처리)
 * @See <a href="https://devjem.tistory.com/m/72">JwtExceptionFilter 1</a>
 * @See <a href="https://jhkimmm.tistory.com/29">JwtExceptionFilter 2</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtExceptionFilter doFilterInternal()");
        try {
            filterChain.doFilter(request, response);
        }  catch(ExpiredJwtException e) {
            // 1. 만료된 토큰 예외처리
            setErrorResponse(response, EXPIRED_TOKEN.getCode(), EXPIRED_TOKEN.getMessage());
        } catch (JwtException e) {
            // 2. 유효하지 않은 토큰 예외처리
            setErrorResponse(response, INVALID_TOKEN.getCode(), INVALID_TOKEN.getMessage());
        } catch(IllegalStateException e) {
            // 3.
            setErrorResponse(response, NON_TOKEN.getCode(), NON_TOKEN.getMessage());
        }
    }

    public void setErrorResponse(HttpServletResponse response, String errorCode, String errorMessage) throws IOException {
        // 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401로 응답
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8"); // 한글 깨짐 문제
        // 응답 바디 설정
        ErrorResponse body = ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
