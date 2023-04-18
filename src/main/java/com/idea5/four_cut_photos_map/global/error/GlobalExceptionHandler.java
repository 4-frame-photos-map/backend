package com.idea5.four_cut_photos_map.global.error;

import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.exception.NonTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * javax.validation.Valid 또는 @Validated binding error가 발생할 경우
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_PARAMETER.getErrorCode(), e.getBindingResult());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    /**
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse>
    handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(),
                e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse>
    handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED.toString(),
                e.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
    /**
     * 비즈니스 로직 실행 중 오류 발생
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<ErrorResponse> handleConflict(BusinessException e) {
        log.error("BusinessException", e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getErrorCode(), e.getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    /**
     * 빈 토큰
     */
    @ExceptionHandler(NonTokenException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDenied(NonTokenException e) {
        log.error("NonTokenException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NON_TOKEN.getErrorCode(), ErrorCode.NON_TOKEN.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 유효하지 않은 토큰
     */
    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidToken(JwtException e) {
        log.error("JwtException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_TOKEN.getErrorCode(), ErrorCode.INVALID_TOKEN.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 만료된 토큰
     */
    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleExpiredToken(ExpiredJwtException e) {
        log.error("ExpiredJwtException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EXPIRED_TOKEN.getErrorCode(), ErrorCode.EXPIRED_TOKEN.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 주로 PathVariable, RequestParam 유효성 검증(@Validation) 실패한 경우
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_PARAMETER.getErrorCode(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * RequestParam 필수 파라미터 누락된 경우
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MISSING_PARAMETER.getErrorCode(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 읽을 수 없는 포맷으로 요청하는 경우(JSON 파싱 문제)
     * - RequestBody 와 형식이 맞지 않거나 JSON 으로 요청하지 않는 경우
     * @See <a href="https://velog.io/@dhwlddjgmanf/%EA%BC%AC%EB%A6%AC%EB%B3%84-%EC%98%A4%EB%A5%98%EC%9D%BC%EC%A7%80-Exception-Handling%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%95%98%EB%A9%B4-%EB%8D%94-%EC%9E%98%ED%95%A0-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C">HttpMessageNotReadableException</>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_JSON.getErrorCode(), ErrorCode.INVALID_JSON.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 나머지 예외 발생
     */
    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}