package com.idea5.four_cut_photos_map.domain.memberTitle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 회원 칭호 종류
@Getter
@AllArgsConstructor
public enum MemberTitleType {
    NEWBIE(1L),
    FIRST_REVIEW(2L),
    MANY_REVIEW(3L),
    FIRST_HEART(4L),
    MANY_HEART(5L);

    private Long code;
}
