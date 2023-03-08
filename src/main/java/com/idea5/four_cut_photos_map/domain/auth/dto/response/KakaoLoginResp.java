package com.idea5.four_cut_photos_map.domain.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 카카오 로그인 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoLoginResp {
    private JwtToken jwtToken;
}
