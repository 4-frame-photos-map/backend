package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberTitleResp {
    private Long id;        // id
    private String name;    // 이름
    private Boolean status; // 획득 여부

    public static MemberTitleResp toDto(MemberTitle memberTitle, boolean status) {
        return MemberTitleResp.builder()
                .id(memberTitle.getId())
                .name(memberTitle.getName())
                .status(status)
                .build();
    }
}
