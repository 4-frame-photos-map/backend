package com.idea5.four_cut_photos_map.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService.MAX_FAVORITE_SHOP_COUNT;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "001", "business Error"),
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "지점을 찾을 수 없습니다."),
    DISTANCE_IS_EMPTY(HttpStatus.BAD_REQUEST, "400", "[distance] 거리는 필수 입력값입니다."),

    INVALID_SHOP_ID(HttpStatus.BAD_REQUEST, "400", "[id] 해당 id는 셀프 즉석사진관에 해당하는 id가 아닙니다. DB에 저장된 데이터 중 다른 업종에 속한 id 입니다."),

    // 인증 관련 오류
    NON_TOKEN(HttpStatus.UNAUTHORIZED, "100", "HTTP Authorization header 에 토큰을 담아 요청해주세요."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "101", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"102", "만료된 토큰입니다."),

    DUPLICATE_FAVORITE(HttpStatus.CONFLICT, "409", "해당 지점은 이미 찜 되어있습니다."),
    DELETED_FAVORITE(HttpStatus.CONFLICT, "409", "해당 지점은 이미 찜 되어있지 않습니다."),
    MEMBER_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 회원 칭호가 존재하지 않습니다."),
    MEMBER_TITLE_NOT_HAD(HttpStatus.NOT_FOUND, "404", "해당 회원이 칭호를 소유하고 있지 않습니다."),
    SHOP_TITLE_LOGS_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 지점은 칭호가 없습니다."),
    SHOP_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "칭호가 없습니다."),
    DUPLICATE_SHOP_TITLE(HttpStatus.CONFLICT, "409", "해당 지점은 이미 타이틀을 보유하고 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "리뷰를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "회원을 찾을 수 없습니다."),
    WRITER_DOES_NOT_MATCH(HttpStatus.BAD_REQUEST, "400", "작성자가 일치하지 않습니다."),
    DUPLICATE_MEMBER_NICKNAME(HttpStatus.CONFLICT, "409", "중복된 닉네임입니다."),
    FAVORITE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "400", String.format("최대 찜 개수 %d개를 초과하였습니다.", MAX_FAVORITE_SHOP_COUNT)),

    //
    INVALID_JSON(HttpStatus.BAD_REQUEST, "001", "Request Body JSON 형식이 잘못되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "002", "파라미터 형식이 잘못되었습니다.");


    private HttpStatus httpStatus;
    private String errorCode;
    private String message;


}
