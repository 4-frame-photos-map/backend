package com.idea5.four_cut_photos_map.domain.review.dto.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberResp {
    private Long id;            // 회원 번호
    private String nickname;    // 닉네임
    private String mainMemberTitle; // 회원 대표 칭호
}
