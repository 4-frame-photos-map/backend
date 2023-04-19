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
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "조회한 지점이 존재하지 않습니다."),
    INVALID_SHOP_ID(HttpStatus.BAD_REQUEST, "400", "[id] 도로명주소나 브랜드명 불일치로 카카오맵에서 일치하는 데이터가 없는 id 입니다. 다른 id로 요청해주세요."),

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
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "브랜드를 찾을 수 없습니다."),
    WRITER_DOES_NOT_MATCH(HttpStatus.BAD_REQUEST, "400", "작성자가 일치하지 않습니다."),
    DUPLICATE_MEMBER_NICKNAME(HttpStatus.CONFLICT, "409", "중복된 닉네임입니다."),
    FAVORITE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "400", String.format("최대 찜 개수 %d개를 초과하였습니다.", MAX_FAVORITE_SHOP_COUNT)),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "429", "일일 카카오 API 호출 한도가 초과되었습니다. 관리자에게 문의 바랍니다."),

    // Request Parameter, Body 관련 오류(0번대)
    INVALID_JSON(HttpStatus.BAD_REQUEST, "001", "Request Body JSON 형식이 잘못되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "002", "파라미터 형식이 잘못되었습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "003", "필수 파라미터가 누락되었습니다"),

    // 파일 업로드 오류
    NOT_IMAGE_FILE(HttpStatus.BAD_REQUEST, "004", "이미지 파일(png, jpeg, gif, webp)이 아닙니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "005", "업로드 가능한 파일 용량을 초과했습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String message;


}
