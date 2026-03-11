package com.idea5.four_cut_photos_map.domain.favorite.entity;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Favorite extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

//    @Version
//    @Setter(AccessLevel.NONE) // 버전은 JPA가 관리하므로 외부 수정을 막음
//    private Long version = 0L;
//
//    public Favorite(Member member, Shop shop) {
//        this.member = member;
//        this.shop = shop;
//    }
}

