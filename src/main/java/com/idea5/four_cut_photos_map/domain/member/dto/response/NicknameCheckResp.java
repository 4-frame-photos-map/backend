package com.idea5.four_cut_photos_map.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 닉네임 중복조회 API 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class NicknameCheckResp {
    private String nickname;// 닉네임
    private Boolean status; // 중복여부
}
