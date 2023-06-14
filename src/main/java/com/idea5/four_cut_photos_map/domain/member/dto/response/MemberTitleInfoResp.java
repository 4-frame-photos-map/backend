package com.idea5.four_cut_photos_map.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberTitleInfoResp {
    private Integer memberTitleCnt; // 회원이 보유한 칭호 수
    private String mainMemberTitle; // 회원의 대표 칭호명
}
