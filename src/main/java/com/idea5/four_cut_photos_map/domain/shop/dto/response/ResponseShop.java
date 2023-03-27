package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

/**
 * 키워드 조회, 전체/브랜드별 조회 공통 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseShop {
    private Long id;
    private String placeName;
    private String roadAddressName;
    private String longitude;
    private String latitude;
    private String distance;
    private boolean canBeAddedToFavorites;

//    @JsonIgnore // 상점이 보유한 칭호가 없다면 null 보다는 응답 데이터에서 제외되는게 더 낫다고 생각
//    private List<String> shopTitles = new ArrayList<>();
//    public void setShopTitles(List<String> shopTitles){ this.shopTitles = shopTitles;}

    static public ResponseShop of(Shop dbShop, KakaoMapSearchDto apiShop){
        return ResponseShop.builder()
                .id(dbShop.getId())
                .placeName(apiShop.getPlaceName())
                .roadAddressName(apiShop.getRoadAddressName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .build();
    }
}