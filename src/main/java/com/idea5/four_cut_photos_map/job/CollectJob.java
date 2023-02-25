package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @See <a href="https://github.com/hayeon17kim/TIL/blob/master/project-badge-system.md">배지 시스템 참고</a>
 */
@Component
@RequiredArgsConstructor
public class CollectJob {
    private final MemberService memberService;
    private final MemberTitleService memberTitleService;

    // 초 분 시 일 월 요일
    @Scheduled(cron = "0 * * * * *")      // TODO: 테스트용 매분마다 실행
//    @Scheduled(cron = "0 0 0 * * *")    // 매일 0시 실행
    public void add() {
        // 회원, 회원칭호 전체조회
        List<MemberTitle> memberTitles = memberTitleService.findAllMemberTitle();
        List<Member> members = memberService.findAll();
        // 회원별로 각 칭호 부여하기
        for(Member member : members) {
            // 회원이 보유한 회원칭호 전체조회
            List<MemberTitle> collectedMemberTitles = memberTitleService.findMemberTitleByMember(member);
            for(MemberTitle memberTitle : memberTitles) {
                // 1. 회원이 보유한 회원칭호는 패스
                if(collectedMemberTitles.contains(memberTitle)) {
                    continue;
                }
                // 2. 회원이 보유하지 않은 회원칭호는 부여기준 검사 -> 부여
                if(memberTitleService.canGiveMemberTitle(member, memberTitle)) {
                    memberTitleService.addMemberTitle(member, memberTitle, false);
                }
            }
        }
    }
}
