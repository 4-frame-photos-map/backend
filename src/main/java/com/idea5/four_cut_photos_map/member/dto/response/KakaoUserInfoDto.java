package com.idea5.four_cut_photos_map.member.dto.response;

import com.idea5.four_cut_photos_map.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoUserInfoDto {
    private Long id;            // 회원번호
    private String nickname;

    public static Member toEntity(KakaoUserInfoDto kakaoUserInfoDto) {
        return Member.builder()
                .kakaoId(kakaoUserInfoDto.getId())
                .nickname(kakaoUserInfoDto.getNickname())
                .build();
    }
}
