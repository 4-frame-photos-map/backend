package com.idea5.four_cut_photos_map.domain.review.entity.score;

import lombok.Getter;

@Getter
public enum PurityScore {
    BAD("청결하지 않아요.", -1),
    UNSELECTED("선택되지 않았습니다.", 0),
    GOOD("청결해요.", 1);


    private PurityScore(String msg, int value){
        this.msg = msg;
        this.value = value;

    }

    private final String msg;
    private final int value;
}
