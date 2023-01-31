package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShopBrand {
    private String placeName; // 장소명
    private String roadAddressName; // 도로명 주소
    private String x; // 경도
    private String y; // 위도
    private String distance; // 거리

    static public ResponseShopBrand of(KakaoResponseDto dto){
        return ResponseShopBrand.builder()
                .placeName(dto.getPlaceName())
                .roadAddressName(dto.getRoadAddressName())
                .x(dto.getX())
                .y(dto.getY())
                .distance(dto.getDistance())
                .build();
    }
}
