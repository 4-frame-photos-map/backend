package com.idea5.four_cut_photos_map.domain.memberTitle.repository;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTitleLogRepository extends JpaRepository<MemberTitleLog, Long> {
    List<MemberTitleLog> findAllByMemberIdOrderByIdAsc(Long memberId);

    List<MemberTitleLog> findByMember(Member member);
}
