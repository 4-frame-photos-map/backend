package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleType;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @See <a href="https://github.com/hayeon17kim/TIL/blob/master/project-badge-system.md">배지 시스템 참고</a>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CollectJob {
    private final MemberService memberService;
    private final MemberTitleService memberTitleService;

    // 초 분 시 일 월 요일
    @Scheduled(cron = "0 * * * * *")      // TODO: 테스트용 매분마다 실행
//    @Scheduled(cron = "0 0 0 * * *")    // 매일 0시 실행
    public void add() {
        // 인증된 API 요청 -> 로그 남기자
        // TODO: 전체 회원 말고 오늘 요청보낸 회원만
        // 회원, 회원칭호 전체조회
        log.info("---Before memberTitleService.findAllMemberTitle()---");
        List<MemberTitle> memberTitles = memberTitleService.findAllMemberTitle();
        log.info("---Before memberService.findAll()---");
        List<Member> members = memberService.findAll();
        // 회원별로 각 칭호 부여하기
        for(Member member : members) {
            // 회원이 보유한 회원칭호 전체조회
            log.info("---Before memberTitleService.findMemberTitleByMember(member)---");
            List<Long> collectedMemberTitles = memberTitleService.findMemberTitleByMember(member);
            for(MemberTitle memberTitle : memberTitles) {
                // 1. 회원이 보유한 회원칭호는 패스
                if(collectedMemberTitles.contains(memberTitle.getId()))
                    continue;
                // 2. 회원이 보유하지 않은 회원칭호는 부여기준 검사 -> 부여
                if(memberTitleService.canGiveMemberTitle(member, memberTitle)) {
                    // 회원가입 칭호와 다른 칭호를 같은 날에 부여 받는 경우 회원가입 칭호를 대표 칭호로 설정
                    boolean isMain = memberTitle.getId() == MemberTitleType.NEWBIE.getCode() ? true : false;
                    memberTitleService.addMemberTitle(member, memberTitle, isMain);
                }
            }
        }
    }
}
