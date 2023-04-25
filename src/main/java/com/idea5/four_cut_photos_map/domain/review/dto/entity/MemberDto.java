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
public class MemberDto {
    private Long id;            // 회원 번호
    private String nickname;    // 카카오 닉네임
}
