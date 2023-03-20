package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoKeywordResponseDto;
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
    private double latitude; // 위도
    private double longitude; // 경도
    private String distance; // 중심좌표까지의 거리

    public static ResponseShop from(Shop shop, KakaoKeywordResponseDto apiShop){
        return ResponseShop.builder()
                .id(shop.getId())
                .placeName(shop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .longitude(Double.parseDouble(apiShop.getLongitude()))
                .latitude(Double.parseDouble(apiShop.getLatitude()))
                .distance(apiShop.getDistance())
                .build();
    }
}
