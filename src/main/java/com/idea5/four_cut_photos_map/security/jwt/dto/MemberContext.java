package com.idea5.four_cut_photos_map.security.jwt.dto;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 가 제공하는 로그인한 사용자 정보 커스텀 클래스
 * @See <a href="https://sol-devlog.tistory.com/3"></>
 */
@Getter
public class MemberContext extends User {
    private final Long id;

    private final Set<GrantedAuthority> authorities;

    public MemberContext(Member member) {
        // username, password, authorities
        super(member.getId().toString(), "", member.getAuthorities());

        this.id = member.getId();
        this.authorities = member.getAuthorities().stream().collect(Collectors.toSet());
    }

    public Member getMember() {
        return Member
                .builder()
                .id(id)
                .build();
    }
}
