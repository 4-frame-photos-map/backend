package com.idea5.four_cut_photos_map.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: global ErrorCode 와 합칠지 상의하기
@Getter
@AllArgsConstructor
public enum ErrorCode {
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰입니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    NON_TOKEN("NON_TOKEN", "HTTP Authorization header 에 토큰을 담아 요청해주세요.");

    private String code;
    private String message;
}
