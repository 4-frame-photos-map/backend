package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShopBrand {
    private Long id;
    private String placeName; // 장소명
    private String roadAddressName; // 도로명 주소
    private String longitude; // 경도
    private String latitude; // 위도
    private String distance; // 거리
    private boolean canBeAddedToFavorites; // 사용자의 찜 여부


    public void setDistance(String distance){
        this.distance = distance;
    }
//    @JsonIgnore // 상점이 보유한 칭호가 없다면 null 보다는 응답 데이터에서 제외되는게 더 낫다고 생각
//    private List<String> shopTitles = new ArrayList<>();
//    public void setShopTitles(List<String> shopTitles){ this.shopTitles = shopTitles;}

    static public ResponseShopBrand of(Shop shop, KakaoResponseDto dto){
        return ResponseShopBrand.builder()
                .id(shop.getId())
                .placeName(dto.getPlaceName())
                .roadAddressName(dto.getRoadAddressName())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .distance(dto.getDistance())
                .build();
    }


}