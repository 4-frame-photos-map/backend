package com.idea5.four_cut_photos_map.domain.memberTitle.entity;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * @See <a href="https://velog.io/@hyeminn/JPA-%EB%91%90%EA%B0%9C-%EC%9D%B4%EC%83%81-%EC%BB%AC%EB%9F%BC-UNIQUE-%EC%A1%B0%EA%B1%B4-%EC%84%A4%EC%A0%95">@UniqueConstraint</a>
 */
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "member_id_member_title_id_unique",
                columnNames = {"member_id", "member_title_id"}
        )})
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
