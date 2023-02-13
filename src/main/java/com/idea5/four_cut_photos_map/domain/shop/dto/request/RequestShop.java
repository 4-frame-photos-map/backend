package com.idea5.four_cut_photos_map.domain.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class RequestShop {

    @NotNull(message = "경도는 필수 입력값 입니다.")
    private Double longitude; // 경도
    @NotNull(message = "위도는 필수 입력값 입니다.")
    private Double latitude; // 위도

}
