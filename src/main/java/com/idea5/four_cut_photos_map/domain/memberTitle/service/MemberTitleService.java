package com.idea5.four_cut_photos_map.domain.memberTitle.service;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitlesResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberTitleService {
    private final MemberTitleRepository memberTitleRepository;
    private final MemberTitleLogRepository memberTitleLogRepository;

    public MemberTitle findById(Long id) {
        return memberTitleRepository.findById(id).orElseThrow(() -> {
            throw new BusinessException(ErrorCode.MEMBER_TITLE_NOT_FOUND);
        });
    }

    // 회원 칭호 정보 조회
    public MemberTitleInfoResp getMemberTitleInfo(Long memberTitleId, Long memberId) {
        MemberTitle memberTitle = findById(memberTitleId);
        // 획득 여부, 대표 칭호 여부 조회
        boolean status = false;
        boolean isMain = false;
        MemberTitleLog memberTitleLog = memberTitleLogRepository.findByMemberIdAndMemberTitleId(memberId, memberTitleId).orElse(null);
        if(memberTitleLog != null) {
            status = true;
            isMain = memberTitleLog.getIsMain();
        }
        return MemberTitleInfoResp.toDto(memberTitle, status, isMain);
    }

    // TODO : 로직 리팩토링
    public MemberTitlesResp getMemberTitles(Member member) {
        // 1. 전체 회원 칭호 조회
        log.info("----Before memberTitleRepository.findAllByOrderByIdAsc()----");
        List<MemberTitle> memberTitles = memberTitleRepository.findAllByOrderByIdAsc();
        // 2. 획득한 회원 칭호 조회
        log.info("----Before memberTitleLogRepository.findAllByMemberOrderByMemberTitleIdAsc(member)----");
        List<MemberTitleLog> myMemberTitleLogs = memberTitleLogRepository.findAllByMember(member);
        // 3. memberTitleId 오름차순 정렬(탐색 성능 개선)
        Collections.sort(myMemberTitleLogs, (o1, o2) -> (int) (o1.getMemberTitle().getId() - o2.getMemberTitle().getId()));
//        List<MemberTitle> myMemberTitles = myMemberTitleLogs.stream()
//                .map(memberTitleLog -> memberTitleLog.getMemberTitle())
//                .collect(Collectors.toList());
        // DTO 변환해서 담기
        int cnt = myMemberTitleLogs.size(); // 획득한 칭호 개수
        int idx = 0; // 탐색 인덱스
        List<MemberTitleResp> memberTitleResps = new ArrayList<>();
        MemberTitleResp mainMemberTitle = null;
        for(MemberTitle mt : memberTitles) {
            MemberTitleLog memberTitleLog = myMemberTitleLogs.get(idx);
            boolean status = false; // 획득 여부
            // 4. 획득한 칭호인지 검사
            if(idx != cnt && memberTitleLog.getMemberTitle().getId().equals(mt.getId())) {
                status = true;
                idx++;
                // 5. 대표 칭호인지 검사
                if(memberTitleLog.getIsMain()) {
                    mainMemberTitle = MemberTitleResp.toDto(mt, status);
                }
            }
            memberTitleResps.add(MemberTitleResp.toDto(mt, status));
        }
        return new MemberTitlesResp(cnt, mainMemberTitle, memberTitleResps);
    }

    public List<MemberTitleLog> findByMember(Member member) {
        return memberTitleLogRepository.findByMember(member);
    }

    // 회원 대표 칭호 수정
    @Transactional
    public void updateMainMemberTitle(Member member, Long memberTitleId) {
        // 1. 기존 회원의 대표 칭호 해제
        log.info("----Before memberTitleLogRepository.findByMemberIdAndIsMainTrue()----");
        MemberTitleLog memberTitleLog = memberTitleLogRepository.findByMemberAndIsMainTrue(member).orElse(null);
        memberTitleLog.cancelMain();
        // 2. 새로운 칭호로 대표 칭호 설정
        log.info("----Before memberTitleLogRepository.findByMemberIdAndMemberTitleId()----");
        MemberTitleLog newMemberTitleLog = memberTitleLogRepository.findByMemberAndMemberTitleId(member, memberTitleId)
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorCode.MEMBER_TITLE_NOT_HAD);
                });
        newMemberTitleLog.registerMain();
    }

    // memberId 를 참조하고 있는 row 모두 삭제
    @Transactional
    public void deleteByMemberId(Long memberId) {
        log.info("----Before memberTitleLogRepository.findByMember()----");
        List<MemberTitleLog> memberTitleLogs = memberTitleLogRepository.findByMember(Member.builder().id(memberId).build());
        log.info("----Before memberTitleLogRepository.delete()----");
        for(MemberTitleLog memberTitleLog : memberTitleLogs) {
            memberTitleLogRepository.delete(memberTitleLog);
        }
    }

    public List<MemberTitle> findAllMemberTitle() {
        return memberTitleRepository.findAllByOrderByIdAsc();
    }

    // 회원이 보유한 MemberTitle id 리스트 조회
    public List<Long> findMemberTitleByMember(Member member) {
        return memberTitleLogRepository.findByMember(member)
                .stream().map(memberTitleLog -> memberTitleLog.getMemberTitle().getId())
                .collect(Collectors.toList());
    }
}
