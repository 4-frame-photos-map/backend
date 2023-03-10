package com.idea5.four_cut_photos_map.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 인 값은 응답에서 제외
public class MemberInfoResp {
    private Long id;
    private String nickname;
    private String mainMemberTitle; // 대표 회원칭호
    private Integer memberTitleCnt; // 회원칭호 개수

    public static MemberInfoResp toDto(Member member, String mainMemberTitle, Integer memberTitleCnt) {
        return MemberInfoResp.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .mainMemberTitle(mainMemberTitle)
                .memberTitleCnt(memberTitleCnt)
                .build();
    }
}
