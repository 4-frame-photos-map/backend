package com.idea5.four_cut_photos_map.domain.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestShop {
    private double latitude; // 위도
    private double longitude; // 경도
}
