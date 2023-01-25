package com.idea5.four_cut_photos_map.member.dto;

import com.idea5.four_cut_photos_map.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoUserInfoParam {
    private Long id;            // 회원번호
    private String nickname;

    public static Member toEntity(KakaoUserInfoParam kakaoUserInfoParam) {
        return Member.builder()
                .kakaoId(kakaoUserInfoParam.getId())
                .nickname(kakaoUserInfoParam.getNickname())
                .build();
    }
}
