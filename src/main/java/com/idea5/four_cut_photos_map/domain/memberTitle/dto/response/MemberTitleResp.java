package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 칭호 전체 조회 응답 DTO
 */
@Getter
@Setter
@Builder
public class MemberTitleResp {
    private Long id;        // 칭호 id
    private String name;    // 칭호명
    private String content; // 칭호 기준
    private char status;    // 회원 본인이 칭호 부여받았는지 여부

    public static MemberTitleResp toDto(MemberTitle memberTitle) {
        return MemberTitleResp.builder()
                .id(memberTitle.getId())
                .name(memberTitle.getName())
                .content(memberTitle.getContent())
                .status('n')
                .build();
    }
}
