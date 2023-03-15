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
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 브랜드가 DB에 존재하지 않습니다."),
    DISTANCE_IS_EMPTY(HttpStatus.BAD_REQUEST, "400", "[distance] 거리는 필수 입력값 입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"401", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "400,", "유효하지 않은 토큰입니다."),
    NON_TOKEN(HttpStatus.BAD_REQUEST, "400", "HTTP Authorization header 에 토큰을 담아 요청해주세요."),
    DUPLICATE_FAVORITE(HttpStatus.CONFLICT, "409", "해당 상점은 이미 찜 되어있습니다."),
    DELETED_FAVORITE(HttpStatus.CONFLICT, "409", "해당 상점은 이미 찜 취소되었습니다."),
    MEMBER_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 회원 칭호가 존재하지 않습니다."),
    MEMBER_TITLE_NOT_HAD(HttpStatus.NOT_FOUND, "404", "해당 회원이 칭호를 소유하고 있지 않습니다."),
    SHOP_TITLE_LOGS_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 상점은 칭호가 없습니다."),
    SHOP_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "칭호가 없습니다."),
    DUPLICATE_SHOP_TITLE(HttpStatus.CONFLICT, "409", "해당 상점은 이미 타이틀을 보유하고 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "리뷰를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "회원을 찾을 수 없습니다."),
    WRITER_DOES_NOT_MATCH(HttpStatus.BAD_REQUEST, "400", "작성자가 일치하지 않습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String message;


}
