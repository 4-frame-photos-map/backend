package com.idea5.four_cut_photos_map.domain.title.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *칭호종류
 */
@Getter
@AllArgsConstructor
public enum TitleType {
    NEWBIE(1),
    FIRST_REVIEW(2),
    MANY_REVIEW(3),
    FIRST_HEART(4),
    MANY_HEART(5);

    private Integer code;
}
