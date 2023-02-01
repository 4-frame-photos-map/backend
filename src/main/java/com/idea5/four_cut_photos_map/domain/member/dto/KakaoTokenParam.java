package com.idea5.four_cut_photos_map.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 카카오 API 호출을 위한 토큰 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class KakaoTokenParam {
    private String accessToken;
    private String refreshToken;
}
