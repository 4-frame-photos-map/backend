package com.idea5.four_cut_photos_map.domain.titleLog.entity;

import com.idea5.four_cut_photos_map.domain.title.entity.Title;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class TitleLog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name ="title_id")
    private Title title;
}
