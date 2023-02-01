package com.idea5.four_cut_photos_map.domain.review.dto.request;

import lombok.Data;

@Data
public class WriteReviewDTO {
    private int starRating;
    private String content;
    private boolean checkPurity;
    private boolean checkRetouch;
    private boolean checkItem;

    public boolean isNotValid() {
        return starRating > 5 || starRating < 1 || content == null || content.trim().length() == 0;
    }

}
