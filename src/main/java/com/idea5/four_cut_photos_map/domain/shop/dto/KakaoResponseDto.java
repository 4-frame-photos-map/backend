package com.idea5.four_cut_photos_map.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {

    private String brand;
    private String address_name;
    private String distance;
    private String phone;
    private String placeName; // ex : 인생네컷 ~점
    private String roadAddressName; // ex : 서울 성동구 서울숲2길 48

    private String x;

    private String y;


}
