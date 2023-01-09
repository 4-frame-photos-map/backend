package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ResponseMarker {
    private String name;// 장소명
    private double latitude; // 위도
    private double longitude; // 경도
    private double distance; // 중심좌표까지의 거리
}
