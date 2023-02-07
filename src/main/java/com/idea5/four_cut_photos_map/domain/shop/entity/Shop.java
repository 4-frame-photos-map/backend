package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.domain.like.entity.Like;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Shop extends BaseEntity {

    private String brand; // 브랜드명

    private String placeName; // 지점명
    private String roadAddressName; // 주소
    private double longitude; // 경도, x
    private double latitude; // 위도, y

    @OneToMany
    @JoinColumn(name = "shop_id")
    private List<Like> likes = new ArrayList<>();

    public Shop(String brand, String placeName, String roadAddressName, double longitude, double latitude) {
        this.brand = brand;
        this.placeName = placeName;
        this.roadAddressName = roadAddressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
