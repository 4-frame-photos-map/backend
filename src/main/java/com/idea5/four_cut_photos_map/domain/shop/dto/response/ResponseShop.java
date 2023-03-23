package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class ResponseShop {
    private long id;
    private String placeName;// 장소명
    private String roadAddressName; // 전체 도로명 주소
    private String latitude; // 위도
    private String longitude; // 경도
    private String distance; // 중심좌표까지의 거리
    private String phone; // todo: 논의 후 필요없다면 제거 필요

    public static ResponseShop from(Shop shop, KakaoResponseDto apiShop){
        return ResponseShop.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .phone(apiShop.getPhone())
                .build();
    }
}
