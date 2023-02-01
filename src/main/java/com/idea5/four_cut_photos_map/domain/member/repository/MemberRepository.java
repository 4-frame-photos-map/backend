package com.idea5.four_cut_photos_map.domain.member.repository;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);
}
