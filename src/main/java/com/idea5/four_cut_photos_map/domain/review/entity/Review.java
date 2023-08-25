package com.idea5.four_cut_photos_map.domain.review.entity;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.dto.request.RequestReviewDto;
import com.idea5.four_cut_photos_map.domain.review.entity.score.ItemScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.PurityScore;
import com.idea5.four_cut_photos_map.domain.review.entity.score.RetouchScore;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Review extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    private Shop shop;

    private int starRating;

    private String content;

    @Enumerated(EnumType.STRING)
    private PurityScore purity;

    @Enumerated(EnumType.STRING)
    private RetouchScore retouch;

    @Enumerated(EnumType.STRING)
    private ItemScore item;

    public Review update(RequestReviewDto dto) {
        this.starRating = dto.getStarRating();
        this.content = dto.getContent();
        this.purity = PurityScore.valueOf(dto.getPurity());
        this.retouch = RetouchScore.valueOf(dto.getRetouch());
        this.item = ItemScore.valueOf(dto.getItem());

        return this;
    }
}
