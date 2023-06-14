package com.idea5.four_cut_photos_map.domain.memberTitle.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class MemberTitle extends BaseEntity {
    @NotNull
    private String name;    // 칭호명

    @NotNull
    private String standard; // 획득 방법

    @NotNull
    private String content; // 설명

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String colorImageUrl; // 칭호 컬러 이미지 URL

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String bwImageUrl; // 칭호 흑백 이미지 URL

    @OneToMany(mappedBy = "memberTitle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTitleLog> memberTitleLogs = new ArrayList<>();

    public MemberTitle(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public MemberTitle(String name, String content, String colorImageUrl, String bwImageUrl) {
        this.name = name;
        this.content = content;
        this.colorImageUrl = colorImageUrl;
        this.bwImageUrl = bwImageUrl;
    }
}
