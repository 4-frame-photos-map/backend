package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // 통합 테스트 제공(애플리케이션을 직접 구동해서 테스트를 진행하는 라이브 테스트 방법)
@Slf4j
@Transactional  // 테스트 후 롤백
@ActiveProfiles("test") // 테스트 수행 시 test profile 사용(application-test.yml)
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

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void init() {
        log.info("---Before init()---");
        memberTitleRepository.save(new MemberTitle("뉴비", "회원가입"));
        memberTitleRepository.save(new MemberTitle("리뷰 첫 걸음", "첫번째 리뷰 작성"));
        memberTitleRepository.save(new MemberTitle("리뷰 홀릭", "리뷰 3개 이상 작성"));
        memberTitleRepository.save(new MemberTitle("찜 첫 걸음", "첫번째 찜 추가"));
        memberTitleRepository.save(new MemberTitle("찜 홀릭", "찜 3개 이상 추가"));

        shopRepository.save(new Shop("인생네컷", "인생네컷 성수점", "서울시"));
        shopRepository.save(new Shop("인생네컷", "인생네컷 잠실점", "서울시"));
        shopRepository.save(new Shop("인생네컷", "인생네컷 강남점", "서울시"));
    }

    @AfterEach
    void after() {
        log.info("---Before after()---");
        databaseCleaner.execute();
    }

    @DisplayName("회원가입한 모든 회원에게 뉴비 칭호 부여, 대표 칭호 자동 설정")
    @Test
    void t1() {
        // given
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
        // given
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
        assertThat(memberTitleLog2.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog2.getIsMain()).isFalse();
        assertThat(memberTitleLog2.getMemberTitle().getName()).isEqualTo("찜 첫 걸음");
        assertThat(memberTitleLog2.getMemberTitle().getContent()).isEqualTo("첫번째 찜 추가");
    }

    @DisplayName("찜 3개 이상 추가한 회원에게 찜 홀릭 칭호 부여")
    @Test
    void t3() {
        // given
        Member member = new Member();
        memberRepository.save(member);

        Shop shop1 = shopRepository.findById(1L).orElse(null);
        Shop shop2 = shopRepository.findById(2L).orElse(null);
        Shop shop3 = shopRepository.findById(3L).orElse(null);
        favoriteRepository.save(new Favorite(member, shop1));
        favoriteRepository.save(new Favorite(member, shop2));
        favoriteRepository.save(new Favorite(member, shop3));

        // when
        collectJob.add();

        // then
        // 칭호 부여 총 3건
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findAll();
        assertThat(memberTitleLogs.size()).isEqualTo(3);

        // 1번 회원 -> 뉴비 칭호 부여, 대표 칭호 자동 설정
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog1.getIsMain()).isTrue();
        assertThat(memberTitleLog1.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog1.getMemberTitle().getContent()).isEqualTo("회원가입");

        // 1번 회원 -> 찜 첫 걸음 칭호 부여
        MemberTitleLog memberTitleLog2 = memberTitleLogs.get(1);
        assertThat(memberTitleLog2.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog2.getIsMain()).isFalse();
        assertThat(memberTitleLog2.getMemberTitle().getName()).isEqualTo("찜 첫 걸음");
        assertThat(memberTitleLog2.getMemberTitle().getContent()).isEqualTo("첫번째 찜 추가");

        // 1번 회원 -> 찜 홀릭 칭호 부여
        MemberTitleLog memberTitleLog3 = memberTitleLogs.get(2);
        assertThat(memberTitleLog3.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog3.getIsMain()).isFalse();
        assertThat(memberTitleLog3.getMemberTitle().getName()).isEqualTo("찜 홀릭");
        assertThat(memberTitleLog3.getMemberTitle().getContent()).isEqualTo("찜 3개 이상 추가");
    }

    @DisplayName("회원이 이미 보유한 칭호는 다시 부여하지 않는다")
    @Test
    void t4() {
        // given
        memberRepository.save(new Member());

        // when
        collectJob.add();
        collectJob.add();

        // then
        // 칭호 부여 총 1건
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findAll();
        assertThat(memberTitleLogs.size()).isEqualTo(1);

        // 1번 회원 -> 뉴비 칭호 부여
        MemberTitleLog memberTitleLog1 = memberTitleLogs.get(0);
        assertThat(memberTitleLog1.getMember().getId()).isEqualTo(1);
        assertThat(memberTitleLog1.getIsMain()).isTrue();
        assertThat(memberTitleLog1.getMemberTitle().getName()).isEqualTo("뉴비");
        assertThat(memberTitleLog1.getMemberTitle().getContent()).isEqualTo("회원가입");
    }
}