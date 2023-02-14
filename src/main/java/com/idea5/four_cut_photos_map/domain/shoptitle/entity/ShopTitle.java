package com.idea5.four_cut_photos_map.domain.shoptitle.entity;

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
public class ShopTitle extends BaseEntity {
    private String name; // 칭호명
    private String content; // 칭호 얻는 조건


}
