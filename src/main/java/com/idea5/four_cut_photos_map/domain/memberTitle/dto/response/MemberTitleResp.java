package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberTitleResp {
    private Long id;        // id
    private String name;    // 이름
    private String imageUrl;// 이미지 URL
    private Boolean isHolding; // 획득 여부
    private Boolean isMain; // 대표칭호 여부

    public static MemberTitleResp toDto(MemberTitle memberTitle, boolean status, boolean isMain) {
        return MemberTitleResp.builder()
                .id(memberTitle.getId())
                .name(memberTitle.getName())
                .imageUrl(status == true ? memberTitle.getColorImageUrl() : memberTitle.getBwImageUrl())
                .isHolding(status)
                .isMain(isMain)
                .build();
    }
}
