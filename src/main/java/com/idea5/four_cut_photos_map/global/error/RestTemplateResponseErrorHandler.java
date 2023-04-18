package com.idea5.four_cut_photos_map.global.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * RestTemplate 통신 응답에서 발생하는 에러 핸들러
 * @See <a href="https://subsequent-shroud-fd5.notion.site/HttpClientErrorException-Unauthorized-401-Unauthorized-7bb0c0d72e284353890e24ba40b679da"></>
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {
    // 상태 코드가 오류인지 검사
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        log.info("---hasError()---");
        HttpStatus statusCode = response.getStatusCode();
        // HTTP 응답코드가 4xx 나 5xx 인 경우 오류로 간주
        return (statusCode.series() == HttpStatus.Series.SERVER_ERROR
                || statusCode.series() == HttpStatus.Series.CLIENT_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info("---handleError()---");
        HttpStatus statusCode = response.getStatusCode();
        // HTTP 응답코드가 4xx 나 5xx 인 경우 예외처리
        if(statusCode.series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        } else if(statusCode.series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
        }
    }
}
