package com.idea5.four_cut_photos_map.domain.review.entity.score;

import lombok.Getter;

@Getter
public enum RetouchScore {
    BAD("보정이 별로에요.", -1),
    UNSELECTED("선택되지 않았습니다", 0),
    GOOD("보정이 좋아요", 1);


    private RetouchScore(String msg, int value){
        this.msg = msg;
        this.value = value;

    }

    private final String msg;
    private final int value;
}
