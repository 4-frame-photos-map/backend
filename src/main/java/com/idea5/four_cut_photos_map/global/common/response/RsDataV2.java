package com.idea5.four_cut_photos_map.global.common.response;


import com.idea5.four_cut_photos_map.global.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RsDataV2<T>{
    private boolean success; // API 호출 실행 결과 (필수)

    private ErrorResponse error;       // ErrorResponse 리소스 (선택)
    private T result;       // API 가 응답해야하는 리소스 (선택)

    public RsDataV2(boolean success, ErrorResponse error) {
        this.success = success;
        this.error = error;
        this.result = null;
    }
}
