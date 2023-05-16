package com.idea5.four_cut_photos_map.domain.memberTitle.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 회원 칭호 전체 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberTitlesResp {
    private Integer holdingCount; // 획득한 칭호 개수
    private MemberTitleResp mainMemberTitle; // 대표 칭호
    private List<MemberTitleResp> memberTitles; // 전체 칭호
}
