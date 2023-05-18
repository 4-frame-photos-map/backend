package com.idea5.four_cut_photos_map.domain.memberTitle.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberTitleServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberTitleService memberTitleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberTitleRepository memberTitleRepository;

    @BeforeEach
    void init() {
        log.info("---Before init()---");
        memberTitleRepository.save(new MemberTitle("뉴비", "회원가입", "뉴비 컬러 이미지", "뉴비 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("리뷰 첫 걸음", "첫번째 리뷰 작성", "리뷰 첫 걸음 컬러 이미지", "리뷰 첫 걸음 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("리뷰 홀릭", "리뷰 3개 이상 작성", "리뷰 홀릭 컬러 이미지", "리뷰 홀릭 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("찜 첫 걸음", "첫번째 찜 추가", "찜 첫 걸음 컬러 이미지", "찜 첫 걸음 흑백 이미지"));
        memberTitleRepository.save(new MemberTitle("찜 홀릭", "찜 3개 이상 추가", "찜 홀릭 컬러 이미지", "찜 홀릭 흑백 이미지"));
    }

//    @Autowired
//    DatabaseCleaner databaseCleaner;
//
//    @AfterEach
//    void clear() {
//        databaseCleaner.execute();
//    }

    @Test
    @DisplayName("1번 회원이 획득하지 않은 2번째 회원 칭호 정보를 조회한다.")
    void t1() {
        // given
        Member member = memberRepository.save(Member.builder().id(1L).nickname("user").build());
        Long memberTitleId = 2L;
        // when
        MemberTitleInfoResp memberTitleInfo = memberTitleService.getMemberTitleInfo(memberTitleId, member.getId());
        // then
        assertAll(
                () -> assertThat(memberTitleInfo.getId()).isEqualTo(2L),
                () -> assertThat(memberTitleInfo.getName()).isEqualTo("리뷰 첫 걸음"),
                () -> assertThat(memberTitleInfo.getContent()).isEqualTo("첫번째 리뷰 작성"),
                () -> assertThat(memberTitleInfo.getImageUrl()).isEqualTo("리뷰 첫 걸음 흑백 이미지"),
                () -> assertThat(memberTitleInfo.getIsHolding()).isEqualTo(false),
                () -> assertThat(memberTitleInfo.getIsMain()).isEqualTo(false)
        );
    }
}