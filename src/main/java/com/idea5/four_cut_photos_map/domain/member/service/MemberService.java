package com.idea5.four_cut_photos_map.domain.member.service;

import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.member.dto.request.MemberUpdateReq;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberInfoResp;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberWithdrawlResp;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleType;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import com.idea5.four_cut_photos_map.global.common.RedisDao;
import com.idea5.four_cut_photos_map.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;
    private final MemberTitleService memberTitleService;
    private final FavoriteService favoriteService;

    // 회원 가져오기
    @Transactional
    public Member getMember(KakaoUserInfoParam kakaoUserInfoParam) {
        // Unique 한 값인 kakaoId 로 조회
        Member member = memberRepository.findByKakaoId(kakaoUserInfoParam.getId()).orElse(null);
        // 신규 사용자인 경우 회원가입
        if(member == null) {
            Member newMember = KakaoUserInfoParam.toEntity(kakaoUserInfoParam);
            // 회원가입 기본 칭호 부여, 대표 칭호로 설정
            log.info("----Before ----");
            memberRepository.save(newMember);
            memberTitleService.addMemberTitle(newMember, MemberTitleType.NEWBIE.getCode(), true);
            return newMember;
        }
        return member;
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    // 회원 id 로 기본 정보 조회
    public MemberInfoResp getMemberInfo(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        log.info("----Before memberTitleService.findByMember(member)----");
        List<MemberTitleLog> memberTitleLogs = memberTitleService.findByMember(member);
        // 대표 칭호 조회
        for(MemberTitleLog memberTitleLog : memberTitleLogs) {
            if(memberTitleLog.getIsMain()) {
                log.info("----Before memberTitleLog.getMemberTitleName()----");
                return MemberInfoResp.toDto(member, memberTitleLog.getMemberTitleName(), memberTitleLogs.size());
            }
        }
        return null;
    }

    // 서비스 로그아웃(accessToken 무효화)
    public void logout(String accessToken) {
        // 1. 회원의 refreshToken 이 있으면 삭제
        Long memberId = jwtProvider.getId(accessToken);
        if(redisDao.hasKey(memberId.toString())) {
            redisDao.deleteValues(memberId.toString());
        }
        // 2. redis 에 해당 accessToken 블랙리스트로 등록
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisDao.setValues(accessToken, "logout", Duration.ofMillis(expiration));
    }

    // 회원 삭제
    @Transactional
    public MemberWithdrawlResp deleteMember(Long id, String accessToken) {
        // 1. 회원의 refreshToken 이 있으면 삭제
        if (redisDao.hasKey(id.toString())) {
            redisDao.deleteValues(id.toString());
        }
        // 2. redis 에 해당 accessToken 블랙리스트로 등록
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisDao.setValues(accessToken, "withdrawl", Duration.ofMillis(expiration));
        // TODO: 양방향 매핑으로 변경할지 고민중
        // TODO: Member 삭제하기 전 Member 를 참조하고 있는 엔티티(MemberTitleLog, Favorite) 먼저 삭제하기
        memberTitleService.deleteByMemberId(id);
        favoriteService.deleteByMemberId(id);
        // 3. DB 에서 회원 삭제
        memberRepository.deleteById(id);
        return new MemberWithdrawlResp(id);
    }

    // 회원 닉네임 수정
    @Transactional
    public void updateNickname(Long id, MemberUpdateReq memberUpdateReq) {
        Member member = findById(id);
        member.updateNickname(memberUpdateReq);
    }

    // 회원 대표칭호 수정
    @Transactional
    public void updateMainMemberTitle(Member member, Long memberTitleId) {
//        Member member = findById(memberId);
        memberTitleService.updateMainMemberTitle(member, memberTitleId);
    }
}
