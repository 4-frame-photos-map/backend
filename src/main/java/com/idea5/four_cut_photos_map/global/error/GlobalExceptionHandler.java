package com.idea5.four_cut_photos_map.global.error;

import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.exception.NonTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * javax.validation.Valid 또는 @Validated binding error가 발생할 경우
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<RsData> handleBindException(BindException e) {
        log.error("handleBindException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(),
                e.getBindingResult());
//        RsData<ErrorResponse> rsData = new RsData<>(400, "handleBindException", errorResponse);

        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }
    /**
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<RsData>
    handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "handleMethodArgumentTypeMismatchException", errorResponse);
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }
    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<RsData>
    handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "handleHttpRequestMethodNotSupportedException", errorResponse);
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(rsData);
    }
    /**
     * 비즈니스 로직 실행 중 오류 발생
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<RsData> handleConflict(BusinessException e) {
        log.error("BusinessException", e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getErrorCode(), e.getMessage());
//        RsData rsData = new RsData(400, "BusinessException", errorResponse);
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(rsData);
    }

    /**
     * 빈 토큰
     */
    @ExceptionHandler(NonTokenException.class)
    protected ResponseEntity<RsData> handleAccessDenied(NonTokenException e) {
        log.error("NonTokenException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NON_TOKEN.getErrorCode(), ErrorCode.NON_TOKEN.getMessage());
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(rsData);
    }

    /**
     * 유효하지 않은 토큰
     */
    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<RsData> handleInvalidToken(JwtException e) {
        log.error("JwtException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_TOKEN.getErrorCode(), ErrorCode.INVALID_TOKEN.getMessage());
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(rsData);
    }

    /**
     * 만료된 토큰
     */
    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<RsData> handleExpiredToken(ExpiredJwtException e) {
        log.error("ExpiredJwtException", e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EXPIRED_TOKEN.getErrorCode(), ErrorCode.EXPIRED_TOKEN.getMessage());
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(rsData);
    }

    /**
     * 주로 PathVariable, RequestParam 유효성 검증(@Validation) 실패한 경우
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<RsData> handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        String[] errorMessages = e.getMessage().split(",");
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(), errorMessages);
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }

    /**
     * 나머지 예외 발생
     */
    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<RsData> handleException(Exception e) {
        log.error("Exception", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "Exception", errorResponse);
        RsData<Object> rsData = new RsData<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
    }
}

