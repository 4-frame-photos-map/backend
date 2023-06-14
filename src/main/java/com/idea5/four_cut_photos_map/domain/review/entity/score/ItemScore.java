package com.idea5.four_cut_photos_map.domain.review.entity.score;

import lombok.Getter;

@Getter
public enum ItemScore {
    BAD("소품이 적어요", -1),
    UNSELECTED("선택되지 않았습니다.", 0),
    GOOD("소품이 다양해요", 1);

    private ItemScore(String msg, int value){
        this.msg = msg;
        this.value = value;

    }

    private final String msg;
    private final int value;
}
