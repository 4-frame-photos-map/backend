package com.idea5.four_cut_photos_map.domain.member.dto.response;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberInfoResp {
    private Long id;
    private String nickname;
    private String mainTitle;   // 대표 칭호

    public static MemberInfoResp toDto(Member member) {
        return MemberInfoResp.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .mainTitle(member.getMainTitle().getName())
                .build();
    }
}
