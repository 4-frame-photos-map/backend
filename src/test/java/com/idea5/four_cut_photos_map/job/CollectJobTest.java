package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ShopRepository shopRepository;

    @BeforeEach
    public void init() {
        memberTitleRepository.save(new MemberTitle("뉴비", "회원가입"));
        memberTitleRepository.save(new MemberTitle("리뷰 첫 걸음", "첫번째 리뷰 작성"));
        memberTitleRepository.save(new MemberTitle("리뷰 홀릭", "리뷰 3개 이상 작성"));
        memberTitleRepository.save(new MemberTitle("찜 첫 걸음", "첫번째 찜 추가"));
        memberTitleRepository.save(new MemberTitle("찜 홀릭", "찜 3개 이상 추가"));

        shopRepository.save(new Shop());
    }

    @DisplayName("회원가입한 모든 회원에게 뉴비 칭호 부여, 대표 칭호 자동 설정")
    @Test
    void t1() {
        // give
        memberRepository.save(new Member());
        memberRepository.save(new Member());

        // when
        collectJob.add();

        // then
        // 칭호 부여 총 2건
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findAll();
        assertThat(memberTitleLogs.size()).isEqualTo(2);

        // 1번 회원 -> 뉴비 칭호 부여
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog1.getIsMain()).isTrue();
        assertThat(memberTitleLog1.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog1.getMemberTitle().getContent()).isEqualTo("회원가입");

        // 2번 회원 -> 뉴비 칭호 부여
        MemberTitleLog memberTitleLog2 = memberTitleLogs.get(1);
        assertThat(memberTitleLog2.getMember().getId()).isEqualTo(2);
        assertThat(memberTitleLog1.getIsMain()).isTrue();
        assertThat(memberTitleLog2.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog2.getMemberTitle().getContent()).isEqualTo("회원가입");
    }

    @DisplayName("첫번째 찜 추가한 회원에게 찜 첫 걸음 칭호 부여")
    @Test
    void t2() {
        // give
        Member member = new Member();
        memberRepository.save(member);

        Shop shop = shopRepository.findById(1L).orElse(null);
        favoriteRepository.save(new Favorite(member, shop));

        // when
        collectJob.add();

        // then
        // 칭호 부여 총 2건
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findAll();
        assertThat(memberTitleLogs.size()).isEqualTo(2);

        // 1번 회원 -> 뉴비 칭호 부여, 대표 칭호 자동 설정
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog1.getIsMain()).isTrue();
        assertThat(memberTitleLog1.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog1.getMemberTitle().getContent()).isEqualTo("회원가입");

        // 1번 회원 -> 찜 첫 걸음 칭호 부여
        MemberTitleLog memberTitleLog2 = memberTitleLogs.get(1);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog2.getIsMain()).isFalse();
        assertThat(memberTitleLog2.getMemberTitle().getName()).isEqualTo("찜 첫 걸음");
        assertThat(memberTitleLog2.getMemberTitle().getContent()).isEqualTo("첫번째 찜 추가");
    }
}