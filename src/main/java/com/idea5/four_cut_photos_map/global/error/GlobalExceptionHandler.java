package com.idea5.four_cut_photos_map.global.error;

import com.idea5.four_cut_photos_map.global.common.response.RsDataV2;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * javax.validation.Valid 또는 @Validated binding error가 발생할 경우
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<RsDataV2> handleBindException(BindException e) {
        log.error("handleBindException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(),
                e.getBindingResult());
//        RsData<ErrorResponse> rsData = new RsData<>(400, "handleBindException", errorResponse);

        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }
    /**
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<RsDataV2>
    handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "handleMethodArgumentTypeMismatchException", errorResponse);
        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(rsData);
    }
    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<RsDataV2>
    handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "handleHttpRequestMethodNotSupportedException", errorResponse);
        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(rsData);
    }
    /**
     * 비즈니스 로직 실행 중 오류 발생
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<RsDataV2> handleConflict(BusinessException e) {
        log.error("BusinessException", e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getErrorCode(), e.getMessage());
//        RsData rsData = new RsData(400, "BusinessException", errorResponse);
        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(rsData);
    }

    /**
     * 권한이 없는 사용자의 요청에 대한 예외 처리
     * 1) 로그인이 필요한 요청 헤더에 토큰이 없는 경우
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<RsDataV2> handleAccessDenied(AccessDeniedException e) {
        log.error("AccessDeniedException", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.toString(), e.getMessage());
//        RsData rsData = new RsData(400, "AccessDeniedException", errorResponse);
        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(rsData);
    }

    /**
     * 나머지 예외 발생
     */
    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<RsDataV2> handleException(Exception e) {
        log.error("Exception", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage());
//        RsData rsData = new RsData(400, "Exception", errorResponse);
        RsDataV2<Object> rsData = new RsDataV2<>(false, errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rsData);
    }
}

