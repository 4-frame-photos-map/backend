package com.idea5.four_cut_photos_map.domain.title.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *칭호종류
 */
@Getter
@AllArgsConstructor
public enum TitleType {
    NEWBIE(0),
    FIRST_REVIEW(1),
    MANY_REVIEW(2),
    FIRST_HEART(3),
    MANY_HEART(4);

    private Integer code;
}
