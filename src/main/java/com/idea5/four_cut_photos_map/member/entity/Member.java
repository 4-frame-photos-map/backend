package com.idea5.four_cut_photos_map.member.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Column(unique = true)
    private Long kakaoId;       // 카카오 회원번호

    private String nickname;    // 닉네임(default kakao nickname)

    @Column(columnDefinition = "TEXT")
    private String accessToken; // jwt access token
}
