package com.idea5.four_cut_photos_map.domain.favorite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class FavoriteRequest {

    @NotNull(message = "경도는 필수 입력값 입니다.")
    private Double longitude;
    @NotNull(message = "위도는 필수 입력값 입니다.")
    private Double latitude;

}