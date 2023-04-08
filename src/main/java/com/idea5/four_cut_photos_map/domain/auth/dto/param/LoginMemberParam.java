package com.idea5.four_cut_photos_map.domain.auth.dto.param;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginMemberParam {
    private Member member;
    private JwtToken jwtToken;
    private boolean isJoin; // 회원가입 여부
}

