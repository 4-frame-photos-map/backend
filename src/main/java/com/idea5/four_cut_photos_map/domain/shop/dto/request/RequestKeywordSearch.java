package com.idea5.four_cut_photos_map.domain.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class RequestKeywordSearch {
    @NotNull(message = "검색 키워드는 필수 입력값 입니다. 지역명이나 지역명+브랜드, 브랜드+지점명을 입력해주세요.")
    private String keyword;
    @NotNull(message = "경도는 필수 입력값 입니다.")
    private double longitude;
    @NotNull(message = "위도는 필수 입력값 입니다.")
    private double latitude;
}