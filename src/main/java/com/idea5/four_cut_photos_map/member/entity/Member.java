package com.idea5.four_cut_photos_map.member.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    // TODO: 이후 활용
    // 현재 회원이 가지고 있는 권한들을 List<GrantedAuthority> 형태로 리턴
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        return authorities;
    }

    // accessToken 변경
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * AccessToken 발급을 위해 회원정보를 기반으로 Claim 객체 생성
     * @return 회원정보를 담고있는 Claim Map 객체
     */
    public Map<String, Object> getAccessTokenClaims() {
        return Util.mapOf(
                "id", getId(),
                "createDate", getCreateDate(),
                "modifyDate", getModifyDate(),
                "nickname", getNickname(),
                "authorities", getAuthorities()
        );
    }
}
