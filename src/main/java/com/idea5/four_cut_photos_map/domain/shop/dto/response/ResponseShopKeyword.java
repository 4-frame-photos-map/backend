package com.idea5.four_cut_photos_map.domain.shop.dto.response;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class ResponseShopKeyword {
    private long id;
    private String placeName;// 장소명
    private String roadAddressName; // 전체 도로명 주소
    private String latitude; // 위도
    private String longitude; // 경도
    private String distance; // 중심좌표까지의 거리
    private boolean canBeAddedToFavorites; // 사용자의 찜 여부

    public static ResponseShopKeyword of(Shop shop, KakaoResponseDto apiShop){
        return ResponseShopKeyword.builder()
                .id(shop.getId())
                .placeName(apiShop.getPlaceName())
                .roadAddressName(shop.getRoadAddressName())
                .longitude(apiShop.getLongitude())
                .latitude(apiShop.getLatitude())
                .distance(apiShop.getDistance())
                .build();
    }
}
