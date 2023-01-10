package com.idea5.four_cut_photos_map.domain.shop.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResponseShopDetail {

    private Long id; // PK
    private String name;// 장소명
    private String address; // 전체 도로명 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private double distance; // 중심좌표까지의 거리

    // todo : Review 추가;
}
