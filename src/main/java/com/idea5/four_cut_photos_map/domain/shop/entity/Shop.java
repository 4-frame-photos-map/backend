package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Shop extends BaseEntity {

    private String brand; // 브랜드명

    private String placeName; // 지점명
    private String roadAddressName; // 주소

    private double longitude; // 경도, x
    private double latitude; // 위도, y
    private Integer favoriteCnt; // 찜 수


    public Shop(String brand, String placeName, String roadAddressName, double longitude, double latitude) {
        this.brand = brand;
        this.placeName = placeName;
        this.roadAddressName = roadAddressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
