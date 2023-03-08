package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore // 상점이 보유한 칭호가 없다면 null 보다는 응답 데이터에서 제외되는게 더 낫다고 생각
    private List<String> shopTitles = new ArrayList<>();

    public void setDistance(String distance){
        this.distance = distance;
    }
    public void setId(Long id){this.id = id;}

    public void setShopTitles(List<String> shopTitles){ this.shopTitles = shopTitles;}

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
