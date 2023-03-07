package com.idea5.four_cut_photos_map.domain.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;

/**
 * 토큰 받기 API 응답 DTO
 * @See<a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response">응답 API</>
 * @See<a href="https://zzang9ha.tistory.com/380">@JsonNaming</>
 */
@Getter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)   // JSON 에서는 snake 표기로 설정
public class KakaoTokenResp {
    private String tokenType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshTokenExpiresIn;
}
