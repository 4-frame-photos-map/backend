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
    DISTANCE_IS_EMPTY(HttpStatus.BAD_REQUEST, "400", "[distance] 거리는 필수 입력값 입니다.");


    private HttpStatus httpStatus;
    private String errorCode;
    private String message;


}
