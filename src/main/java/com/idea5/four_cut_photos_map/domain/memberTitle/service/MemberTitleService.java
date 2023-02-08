package com.idea5.four_cut_photos_map.domain.memberTitle.service;

import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberTitleService {
    private final MemberTitleRepository memberTitleRepository;
    private final MemberTitleLogRepository memberTitleLogRepository;
    private final MemberRepository memberRepository;

    public MemberTitle findById(Long id) {
        return memberTitleRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("memberTitle 없음");
        });
    }

    // 회원 칭호 정보 조회
    public MemberTitleInfoResp getMemberTitleInfo(Long id) {
        MemberTitle memberTitle = findById(id);
        return MemberTitleInfoResp.toDto(memberTitle);
    }

    // TODO : 로직 리팩토링
    public List<MemberTitleResp> getMemberTitles(Long memberId) {
        // 1. 전체 칭호
        log.info("MemberTitle 전체 조회");
        List<MemberTitle> memberTitles = memberTitleRepository.findAllByOrderByIdAsc();
        // 2. 회원이 갖고 있는 칭호
        log.info("내 MemberTitleLog 조회");
        List<MemberTitleLog> myMemberTitleLogs = memberTitleLogRepository.findAllByMemberIdOrderByIdAsc(memberId);
//        Member member = memberRepository.findById(memberId).orElse(null);
//        List<MemberTitleLog> myMemberTitleLogs = member.getMemberTitleLogs();
        log.info("내 MemberTitle 조회");
        List<MemberTitle> myMemberTitles = myMemberTitleLogs.stream()
                .map(memberTitleLog -> memberTitleLog.getMemberTitle())
                .collect(Collectors.toList());
        // 3. DTO 변환해서 담기
        List<MemberTitleResp> memberTitleResps = new ArrayList<>();
        for(MemberTitle mt : memberTitles) {
            MemberTitleResp memberTitleResp = MemberTitleResp.toDto(mt);
            // 내가 갖고 있는 칭호는 상태를 y 로 바꾸기
            if(myMemberTitles.contains(mt)) {
                memberTitleResp.setStatus('y');
            }
            memberTitleResps.add(memberTitleResp);
        }
        return memberTitleResps;
    }
}
