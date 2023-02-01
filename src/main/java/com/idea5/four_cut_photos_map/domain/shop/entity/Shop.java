package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.domain.like.entity.Like;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Shop extends BaseEntity {

    private String brand; // 브랜드명

    private String name; // 지점명
    private String address; // 주소
    private double longitude; // 경도, x
    private double latitude; // 위도, y

    @OneToMany(mappedBy = "shop")
    @Builder.Default
    @ToString.Exclude
    private List<Review> reviews = new LinkedList<>();

    @OneToMany
    @JoinColumn(name = "shop_id")
    private List<Like> likes = new ArrayList<>();

    public Shop(String brand, String name, String address, double longitude, double latitude) {
        this.brand = brand;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addReview(Review review) {
        review.setShop(this);
        this.reviews.add(review);
    }

}
