package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShopMarker {

    private Long id;
    private String placeName; // 장소명
    private String roadAddressName; // 도로명 주소
    private String x; // 경도
    private String y; // 위도
    private String phone; // 번호
    private String distance; // 거리

    public void setDistance(String distance){
        this.distance = distance;
    }
    public void setId(Long id){this.id = id;}

    static public ResponseShopMarker of(KakaoResponseDto dto){
        return ResponseShopMarker.builder()
                .placeName(dto.getPlaceName())
                .roadAddressName(dto.getRoadAddressName())
                .phone(dto.getPhone())
                .x(dto.getX())
                .y(dto.getY())
                .distance(dto.getDistance())
                .build();
    }
}
