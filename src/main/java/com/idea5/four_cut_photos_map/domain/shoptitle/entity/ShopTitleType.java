package com.idea5.four_cut_photos_map.domain.shoptitle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ShopTitleType {
    HOT_PLACE(1L, "핫플레이스", "지난 달 찜 수 3개 이상", "사람들이 주로 이용하는 포토부스에요."),
    GOOD_CLEANLINESS(2L, "청결한 지점", "지난 달 리뷰 3개 이상, 청결 점수 평균 0.8점 이상", "매장이 깔끔해요");
//    GOOD_RETOUCHING(3L, "보정 양호", "보정 점수 4점 이상", "사진이 잘 나와요."),
//    GOOD_PROP(4L, "소품 양호", "소품 점수 4점 이상", "다양하게 연출하기 좋아요.");

    private Long id;
    private String name;
    private String conditions;
    private String content;
}
