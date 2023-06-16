package com.idea5.four_cut_photos_map.domain.memberTitle.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitlesResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.DatabaseCleaner;
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

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.MEMBER_TITLE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberTitleServiceTest {
    @Autowired
    private MemberTitleService memberTitleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberTitleRepository memberTitleRepository;

    @Autowired
    private MemberTitleLogRepository memberTitleLogRepository;

    @BeforeEach
    void init() {
        log.info("---Before init()---");
        memberTitleRepository.save(new MemberTitle("뉴비", "회원가입", "회원가입을 했어요. 저희 서비스 이용을 응원해요!", "뉴비 컬러 이미지", "뉴비 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("리뷰 첫 걸음", "첫번째 리뷰 작성", "리뷰를 작성했어요.", "리뷰 첫 걸음 컬러 이미지", "리뷰 첫 걸음 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("리뷰 홀릭", "리뷰 3개 작성", "벌써 리뷰를 3회 작성했어요.", "리뷰 홀릭 컬러 이미지", "리뷰 홀릭 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("찜 첫 걸음", "첫번째 찜 추가", "찜한 지점이 생겼어요.", "찜 첫 걸음 컬러 이미지", "찜 첫 걸음 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("찜 홀릭", "찜 3개 추가", "벌써 3개의 지점을 찜 했어요.", "찜 홀릭 컬러 이미지", "찜 홀릭 흑백 이미지"));
    }

    @Autowired
    DatabaseCleaner databaseCleaner;

    @AfterEach
    void clear() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("1번 회원이 획득하지 않은 2번 칭호 정보를 조회한다.")
    void t1() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        Long memberTitleId = 2L;
        // when
        MemberTitleResp memberTitleInfo = memberTitleService.getMemberTitle(memberTitleId, member);
        // then
        assertAll(
                () -> assertThat(memberTitleInfo.getId()).isEqualTo(2L),
                () -> assertThat(memberTitleInfo.getName()).isEqualTo("리뷰 첫 걸음"),
                () -> assertThat(memberTitleInfo.getStandard()).isEqualTo("첫번째 리뷰 작성"),
                () -> assertThat(memberTitleInfo.getContent()).isEqualTo("리뷰를 작성했어요."),
                () -> assertThat(memberTitleInfo.getImageUrl()).isEqualTo("리뷰 첫 걸음 흑백 이미지"),
                () -> assertThat(memberTitleInfo.getIsHolding()).isEqualTo(false),
                () -> assertThat(memberTitleInfo.getIsMain()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("1번 회원이 획득하고 대표 칭호로 설정된 1번 칭호 정보를 조회한다.")
    void t2() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        Long memberTitleId = 1L;
        MemberTitle memberTitle = memberTitleRepository.findById(memberTitleId).orElse(null);
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, true));
        // when
        MemberTitleResp memberTitleInfo = memberTitleService.getMemberTitle(memberTitleId, member);
        // then
        assertAll(
                () -> assertThat(memberTitleInfo.getId()).isEqualTo(1L),
                () -> assertThat(memberTitleInfo.getName()).isEqualTo("뉴비"),
                () -> assertThat(memberTitleInfo.getStandard()).isEqualTo("회원가입"),
                () -> assertThat(memberTitleInfo.getContent()).isEqualTo("회원가입을 했어요. 저희 서비스 이용을 응원해요!"),
                () -> assertThat(memberTitleInfo.getImageUrl()).isEqualTo("뉴비 컬러 이미지"),
                () -> assertThat(memberTitleInfo.getIsHolding()).isEqualTo(true),
                () -> assertThat(memberTitleInfo.getIsMain()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("1번 회원이 획득하고 대표 칭호로 설정되지 않은 1번 칭호 정보를 조회한다.")
    void t3() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        Long memberTitleId = 1L;
        MemberTitle memberTitle = memberTitleRepository.findById(memberTitleId).orElse(null);
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, false));
        // when
        MemberTitleResp memberTitleInfo = memberTitleService.getMemberTitle(memberTitleId, member);
        // then
        assertAll(
                () -> assertThat(memberTitleInfo.getId()).isEqualTo(1L),
                () -> assertThat(memberTitleInfo.getName()).isEqualTo("뉴비"),
                () -> assertThat(memberTitleInfo.getStandard()).isEqualTo("회원가입"),
                () -> assertThat(memberTitleInfo.getContent()).isEqualTo("회원가입을 했어요. 저희 서비스 이용을 응원해요!"),
                () -> assertThat(memberTitleInfo.getImageUrl()).isEqualTo("뉴비 컬러 이미지"),
                () -> assertThat(memberTitleInfo.getIsHolding()).isEqualTo(true),
                () -> assertThat(memberTitleInfo.getIsMain()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("1번 회원이 존재하지 않는 6번 칭호 정보를 조회한다.")
    void t4() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        Long memberTitleId = 6L;
        // when, then
        BusinessException e = assertThrows(BusinessException.class, () ->
                memberTitleService.getMemberTitle(memberTitleId, member)
        );
        assertThat(e.getMessage()).isEqualTo(MEMBER_TITLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("1번 회원이 전체 회원 칭호를 조회한다. 1, 2번 칭호를 보유했고 1번 칭호는 대표 칭호이다.")
    void t5() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        long memberTitleId1 = 1L;
        long memberTitleId2 = 2L;
        MemberTitle memberTitle1 = memberTitleRepository.findById(memberTitleId1).orElse(null);
        MemberTitle memberTitle2 = memberTitleRepository.findById(memberTitleId2).orElse(null);
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle1, true));
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle2, false));
        // when
        MemberTitlesResp memberTitleResp = memberTitleService.getMemberTitles(member);
        // then
        List<MemberTitleResp> memberTitles = memberTitleResp.getMemberTitles();
        MemberTitleResp mainMemberTitle = memberTitleResp.getMainMemberTitle();
        int holdingCount = memberTitleResp.getHoldingCount();
        assertSoftly(softly -> {
            softly.assertThat(holdingCount).isEqualTo(2);
            softly.assertThat(mainMemberTitle.getId()).isEqualTo(memberTitleId1);

            MemberTitleResp mt1 = memberTitles.get(0);
            softly.assertThat(mt1.getIsHolding()).isEqualTo(true);
            softly.assertThat(mt1.getImageUrl()).isEqualTo("뉴비 컬러 이미지");
            softly.assertThat(mt1.getIsMain()).isEqualTo(true);

            MemberTitleResp mt2 = memberTitles.get(1);
            softly.assertThat(mt2.getIsHolding()).isEqualTo(true);
            softly.assertThat(mt2.getImageUrl()).isEqualTo("리뷰 첫 걸음 컬러 이미지");
            softly.assertThat(mt2.getIsMain()).isEqualTo(false);

            MemberTitleResp mt3 = memberTitles.get(2);
            softly.assertThat(mt3.getIsHolding()).isEqualTo(false);
            softly.assertThat(mt3.getImageUrl()).isEqualTo("리뷰 홀릭 흑백 이미지");
            softly.assertThat(mt3.getIsMain()).isEqualTo(false);

            MemberTitleResp mt4 = memberTitles.get(3);
            softly.assertThat(mt4.getIsHolding()).isEqualTo(false);
            softly.assertThat(mt4.getImageUrl()).isEqualTo("찜 첫 걸음 흑백 이미지");
            softly.assertThat(mt4.getIsMain()).isEqualTo(false);

            MemberTitleResp mt5 = memberTitles.get(4);
            softly.assertThat(mt5.getIsHolding()).isEqualTo(false);
            softly.assertThat(mt5.getImageUrl()).isEqualTo("찜 홀릭 흑백 이미지");
            softly.assertThat(mt5.getIsMain()).isEqualTo(false);
        });
    }

    @Test
    @DisplayName("1번 회원이 2번 칭호를 대표칭호로 설정한다. 1번 대표칭호 설정 해제, 2번 대표칭호 설정")
    void t6() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        long memberTitleId1 = 1L;
        long memberTitleId2 = 2L;
        MemberTitle memberTitle1 = memberTitleRepository.findById(memberTitleId1).orElse(null);
        MemberTitle memberTitle2 = memberTitleRepository.findById(memberTitleId2).orElse(null);
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle1, true));
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle2, false));
        // when
        memberTitleService.updateMainMemberTitle(member, memberTitleId2);
        // then
        MemberTitleResp mt1 = memberTitleService.getMemberTitle(memberTitleId1, member);
        MemberTitleResp mt2 = memberTitleService.getMemberTitle(memberTitleId2, member);

        assertSoftly(softly -> {
            softly.assertThat(mt1.getIsMain()).isEqualTo(false);
            softly.assertThat(mt2.getIsMain()).isEqualTo(true);
        });
    }
}