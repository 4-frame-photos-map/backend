package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class ResponseMarker {

    private Long id; // PK
    private String name;// 장소명
    private String latitude; // 위도
    private String longitude; // 경도
    private String distance; // 중심좌표까지의 거리


    public static ResponseMarker of(Shop shop){
        return ResponseMarker.builder()
                .id(shop.getId())
                .name(shop.getPlaceName())
                .build();
    }

    public void setDistance(String distance){
        this.distance = distance;
    }

}
