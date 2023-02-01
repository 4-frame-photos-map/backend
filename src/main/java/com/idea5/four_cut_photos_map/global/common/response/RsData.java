package com.idea5.four_cut_photos_map.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idea5.four_cut_photos_map.global.error.ErrorResponse;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsData<T> {
    private boolean success; // API 호출 실행 결과 (필수)
    private String message;

    private ErrorResponse error;       // ErrorResponse 리소스 (선택)
    private T result;       // API 가 응답해야하는 리소스 (선택)

    // 성공 응답
    public RsData(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    // 실패 응답
    public RsData(boolean success, ErrorResponse error) {
        this.success = success;
        this.error = error;
    }
}
