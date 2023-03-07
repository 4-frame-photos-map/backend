package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class CollectJobTest {
    @Autowired
    private CollectJob collectJob;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberTitleRepository memberTitleRepository;

    @Autowired
    private MemberTitleLogRepository memberTitleLogRepository;

    @DisplayName("회원가입한 모든 회원에게 뉴비 칭호 부여")
    @Test
    void t1() {
        // give
        Member member1 = new Member();
        Member member2 = new Member();
        memberRepository.save(member1);
        memberRepository.save(member2);

        MemberTitle memberTitle = new MemberTitle("뉴비", "회원가입");
        memberTitleRepository.save(memberTitle);

        // when
        collectJob.add();

        // then
        // 칭호 부여 총 2건
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findAll();
        assertThat(memberTitleLogs.size()).isEqualTo(2);

        // 1번 회원 -> 뉴비 칭호 부여
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog1.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog1.getMemberTitle().getContent()).isEqualTo("회원가입");

        // 2번 회원 -> 뉴비 칭호 부여
        MemberTitleLog memberTitleLog2 = memberTitleLogs.get(1);
        assertThat(memberTitleLog2.getMember().getId()).isEqualTo(2);
        assertThat(memberTitleLog2.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog2.getMemberTitle().getContent()).isEqualTo("회원가입");
    }
}