package com.idea5.four_cut_photos_map.domain.auth.dto.response;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class KakaoUserInfoParam {
    private Long id;            // 회원번호
    private String nickname;

    public static Member toEntity(Long kakaoId, String nickname, String kakaoRefreshToken) {
        return Member.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .kakaoRefreshToken(kakaoRefreshToken)
                .build();
    }

    // 닉네임 설정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
