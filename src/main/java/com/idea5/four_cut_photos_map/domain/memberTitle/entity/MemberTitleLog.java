package com.idea5.four_cut_photos_map.domain.memberTitle.entity;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class MemberTitleLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_title_id")
    private MemberTitle memberTitle;

    private Boolean isMain; // 대표 칭호 여부

    // 칭호명 조회
    public String getMemberTitleName() {
        return getMemberTitle().getName();
    }

    // 대표 칭호 설정
    public void registerMain() {
        this.isMain = true;
    }

    // 대표 칭호 설정
    public void cancelMain() {
        this.isMain = false;
    }
}
