package com.idea5.four_cut_photos_map.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewMemberDto {
    private Long id;            // 회원 번호
    private String nickname;    // 카카오 닉네임
}
