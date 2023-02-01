package com.idea5.four_cut_photos_map.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "001", "business Error"),
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "상점을 찾을 수 없습니다."),
    DISTANCE_IS_EMPTY(HttpStatus.BAD_REQUEST, "400", "[distance] 거리는 필수 입력값 입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"401", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "400,", "유효하지 않은 토큰입니다."),
    NON_TOKEN(HttpStatus.BAD_REQUEST, "400", "HTTP Authorization header 에 토큰을 담아 요청해주세요."),
    MEMBER_MISMATCH(HttpStatus.FORBIDDEN, "403", "조회하려는 멤버 정보와 현재 로그인 유저 정보가 일치하지 않습니다. 다시 로그인을 해주세요.");


    private HttpStatus httpStatus;
    private String errorCode;
    private String message;


}
