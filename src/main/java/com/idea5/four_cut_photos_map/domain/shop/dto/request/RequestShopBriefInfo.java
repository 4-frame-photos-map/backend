package com.idea5.four_cut_photos_map.domain.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class RequestShopBriefInfo {
    @NotBlank(message = "지점명은 필수 입력값 입니다.")
    private String placeName;
    @NotBlank(message = "장소 URL은 필수 입력값 입니다.")
    private String placeUrl;
    @NotBlank(message = "거리는 필수 입력값 입니다.")
    private String distance;
}
