package com.idea5.four_cut_photos_map.domain.shop.dto;

import com.idea5.four_cut_photos_map.global.util.DocumentManagement;
import com.idea5.four_cut_photos_map.global.util.DocumentManagement.Document;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KakaoResponseDto {

    private String brand;
    private String address_name;
    private String distance;
    private String phone;
    private String placeName; // ex : 인생네컷 ~점
    private String roadAddressName; // ex : 서울 성동구 서울숲2길 48

    private String x;

    private String y;

    public static KakaoResponseDto from (Document document, String brandName){
        return KakaoResponseDto.builder()
                .address_name(document.getAddress_name())
                .placeName(document.getPlace_name())
                .roadAddressName(document.getRoad_address_name())
                .distance(document.getDistance())
                .x(document.getX())
                .y(document.getY())
                .brand(brandName)
                .phone(document.getPhone()).build();
    }


}
