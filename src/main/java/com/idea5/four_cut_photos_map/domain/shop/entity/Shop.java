package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@DynamicUpdate
@Table(indexes = {
        @Index(name ="idx_shop_address", columnList ="address"),
        @Index(name="idx_shop_brand", columnList="brand_id")})
public class Shop extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    private String placeName;
    private String address;
    private Integer favoriteCnt;
    private Integer reviewCnt;
    private Double starRatingAvg;


    public Shop(String placeName, String address) {
        this.placeName = placeName;
        this.address = address;
    }

    public Shop(Long id, int favoriteCnt) {
        super.setId(id);
        this.favoriteCnt = favoriteCnt;
    }

    public Shop(Long id, int reviewCnt, double starRatingAvg) {
        super.setId(id);
        this.reviewCnt = reviewCnt;
        this.starRatingAvg = starRatingAvg;
    }
}