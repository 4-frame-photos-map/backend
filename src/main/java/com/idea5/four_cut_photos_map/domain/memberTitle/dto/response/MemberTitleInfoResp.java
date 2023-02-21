package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 칭호 정보 응답 DTO
 */
@Getter
@Setter
@Builder
public class MemberTitleInfoResp {
    private Long id;
    private String name;
    private String content;

    public static MemberTitleInfoResp toDto(MemberTitle memberTitle) {
        return MemberTitleInfoResp.builder()
                .id(memberTitle.getId())
                .name(memberTitle.getName())
                .content(memberTitle.getContent())
                .build();
    }
}
