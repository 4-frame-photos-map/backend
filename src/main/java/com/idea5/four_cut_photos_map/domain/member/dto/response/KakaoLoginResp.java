package com.idea5.four_cut_photos_map.domain.member.dto.response;

import com.idea5.four_cut_photos_map.domain.member.dto.KakaoTokenParam;
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
public class KakaoLoginResp {
    private KakaoTokenParam kakaoToken;
    private JwtToken jwtToken;
}
