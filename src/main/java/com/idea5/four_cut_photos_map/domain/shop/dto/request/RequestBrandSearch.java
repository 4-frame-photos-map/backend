package com.idea5.four_cut_photos_map.domain.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RequestBrandSearch {

    @NotBlank(message = "브랜드는 필수 입력값 입니다.")
    private String brand; // 위도
    @NotNull(message = "경도는 필수 입력값 입니다.")
    private Double longitude; // 경도
    @NotNull(message = "위도는 필수 입력값 입니다.")
    private Double latitude; // 위도

}
