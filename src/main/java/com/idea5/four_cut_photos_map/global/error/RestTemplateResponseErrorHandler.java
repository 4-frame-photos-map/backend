package com.idea5.four_cut_photos_map.global.error;

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
@Component
@Slf4j
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {
    // 상태 코드가 오류인지 검사
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        log.info("statusCode=" + statusCode);
        log.info(response.getBody().toString());
        // 5xx 만 오류로 간주
        return (statusCode.series() == HttpStatus.Series.SERVER_ERROR
                || statusCode.series() == HttpStatus.Series.CLIENT_ERROR);
    }
}
