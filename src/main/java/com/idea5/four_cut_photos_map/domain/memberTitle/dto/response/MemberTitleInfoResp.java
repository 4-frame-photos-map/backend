package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 칭호 단건 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberTitleInfoResp {
    private Long id;
    private String name;    // 이름
    private String content; // 부여기준(획득방법)
    private String imageUrl;// 이미지 URL
    private Boolean status; // 획득여부
    private Boolean isMain; // 대표칭호 여부

    public static MemberTitleInfoResp toDto(MemberTitle memberTitle, boolean status, boolean isMain) {
        return MemberTitleInfoResp.builder()
                .id(memberTitle.getId())
                .name(memberTitle.getName())
                .content(memberTitle.getContent())
                .imageUrl(memberTitle.getImageUrl())
                .status(status)
                .isMain(isMain)
                .build();
    }
}
