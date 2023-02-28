package com.idea5.four_cut_photos_map.domain.shoptitle.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class ShopTitle extends BaseEntity {
    @Column(unique=true)
    private String name; // 칭호명
    private String conditions; // 칭호 획득 조건
    private String content; // 칭호 부연 설명




}
