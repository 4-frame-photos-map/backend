package com.idea5.four_cut_photos_map.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 공통 응답 Body DTO
 * @See <a href="http://blog.storyg.co/rest-api-response-body-best-pratics">REST API Response Body 형식</a>
 */
@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private int code;       // 처리 결과 상태
    private String message; // 처리 결과 상태 메시지(개발자들과 서비스를 운영하는 자를 위한 메시지)
    private T result;       // API 가 응답해야하는 리소스

    public static <T> RsData<T> of(int code, String message, T result) {
        return new RsData<>(code, message, result);
    }

    public static <T> RsData<T> of(int code, String message) {
        return of(code, message, null);
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isFail() {
        return isSuccess() == false;
    }
}
