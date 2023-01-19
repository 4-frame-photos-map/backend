package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import com.idea5.four_cut_photos_map.review.entity.Review;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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
    private double latitude; // 위도
    private double longitude; // 경도

    @OneToMany(mappedBy = "shop", fetch = FetchType.EAGER)
    @Builder.Default
    @ToString.Exclude
    private List<Review> reviewList = new LinkedList<>();

    public void addReview(Review review) {
        review.setShop(this);
        this.reviewList.add(review);
    }
}
