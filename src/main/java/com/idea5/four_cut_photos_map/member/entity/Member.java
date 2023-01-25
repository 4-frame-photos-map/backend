package com.idea5.four_cut_photos_map.member.entity;

import com.idea5.four_cut_photos_map.domain.like.entity.Like;
import com.idea5.four_cut_photos_map.domain.titleLog.entity.TitleLog;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<Like> likes = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<TitleLog> titleLogs = new ArrayList<>();

    // TODO: 이후 활용
    // 현재 회원이 가지고 있는 권한들을 List<GrantedAuthority> 형태로 리턴
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        return authorities;
    }
}
