package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Shop extends BaseEntity {

    private String name; // 브랜드명
    private String address; // 주소
    private double latitude; // 위도
    private double longitude; // 경도
}
