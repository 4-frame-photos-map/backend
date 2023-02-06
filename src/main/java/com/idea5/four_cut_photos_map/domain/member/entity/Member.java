package com.idea5.four_cut_photos_map.domain.member.entity;

import com.idea5.four_cut_photos_map.domain.like.entity.Like;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
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

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<Like> likes = new ArrayList<>();

    @Builder.Default    // 빌더 패턴으로 객체 생성시 속성 기본값 지정
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTitleLog> memberTitleLogs = new ArrayList<>();

    // TODO: 이후 활용
    // 현재 회원이 가지고 있는 권한들을 List<GrantedAuthority> 형태로 리턴
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        return authorities;
    }

    // 대표 칭호
    public MemberTitle getMainMemberTitle() {
        for(MemberTitleLog memberTitleLog : getMemberTitleLogs()) {
            if(memberTitleLog.getIsMain()) {
                return memberTitleLog.getMemberTitle();
            }
        }
        return null;
    }

    public Integer getMemberTitleCnt() {
        return memberTitleLogs.size();
    }
}
