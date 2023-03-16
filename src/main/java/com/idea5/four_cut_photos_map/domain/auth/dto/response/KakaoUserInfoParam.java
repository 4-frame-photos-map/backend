package com.idea5.four_cut_photos_map.domain.auth.dto.response;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.util.Util;
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

    public static Member toEntity(KakaoUserInfoParam kakaoUserInfoParam) {
        return Member.builder()
                .kakaoId(kakaoUserInfoParam.getId())
                .nickname(kakaoUserInfoParam.getNickname())
                .build();
    }

    // 유니크한 닉네임으로
    public void updateUniqueNickname() {
        this.nickname += Util.generateRandomNumber(4);
    }
}
