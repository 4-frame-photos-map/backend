package com.idea5.four_cut_photos_map.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.domain.auth.service.KakaoService;
import com.idea5.four_cut_photos_map.domain.member.dto.request.MemberUpdateReq;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberInfoResp;
import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberWithdrawlResp;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final String BEARER_TOKEN_PREFIX = "Bearer ";

    // 회원 기본정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ResponseEntity<RsData> getProfile(@AuthenticationPrincipal MemberContext memberContext) {
        MemberInfoResp memberInfoResp = memberService.getMemberInfo(memberContext.getId());
        return new ResponseEntity<>(
                new RsData<>(true, "회원 정보 조회 성공", memberInfoResp),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/nickname")
    public ResponseEntity<RsData> updateNickname(
            @AuthenticationPrincipal MemberContext memberContext,
            @RequestBody @Valid MemberUpdateReq memberUpdateReq
    ) {
        memberService.updateNickname(memberContext.getId(), memberUpdateReq);
        return new ResponseEntity<>(
                new RsData(true, "회원 닉네임 수정 성공"),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/main-title/{member-title-id}")
    public ResponseEntity<RsData> updateMainMemberTitle(
            @AuthenticationPrincipal MemberContext memberContext,
            @PathVariable(value = "member-title-id") Long memberTitleId
    ) {
        // TODO: 2가지 방식 고민중
        // 1. 기존처럼 member 객체를 넘기는 방법
        // 2. memberId 만 넘기고 실질적으로 조회쿼리가 날라가는 memberTitleService.updateMainMemberTitle() 내에서 member 객체를 만들어 사용하는 방식
        memberService.updateMainMemberTitle(memberContext.getMember(), memberTitleId);
        return new ResponseEntity<>(
                new RsData(true, "회원 대표 칭호 수정 성공"),
                HttpStatus.OK);
    }

    /**
     * 회원탈퇴
     * @param jwtToken 서비스에서 발급한 jwt accessToken
     * @param kakaoAccessToken kakao 에서 발급받은 accessToken
     * @param memberContext
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("")
    public ResponseEntity<RsData> deleteMember(
            @RequestHeader("Authorization") String jwtToken,
            @RequestHeader("kakao-atk") String kakaoAccessToken,
            @RequestHeader("kakao-rtk") String kakaoRefreshToken,
            @AuthenticationPrincipal MemberContext memberContext,
            HttpSession session
    ) throws JsonProcessingException {
        // TODO: 카카오 토큰을 세션에서 가져오는 것으로 변경하기, refreshToken null 일 경우 처리
        log.info("kakao-atk=" + session.getAttribute("kakaoAccessToken"));
        log.info("kakao-rtk=" + session.getAttribute("kakaoRefreshToken"));
        String jwtAccessToken = jwtToken.substring(BEARER_TOKEN_PREFIX.length());
        kakaoAccessToken = kakaoAccessToken.substring(BEARER_TOKEN_PREFIX.length());
        kakaoRefreshToken = kakaoRefreshToken.substring(BEARER_TOKEN_PREFIX.length());
        // 1. 카카오 토큰 만료시 토큰 갱신하기
        if(kakaoService.isExpiredAccessToken(kakaoAccessToken)) {
            kakaoAccessToken = kakaoService.refresh(kakaoRefreshToken);
        }
        // 2. 연결 끊기
        kakaoService.disconnect(kakaoAccessToken);
        MemberWithdrawlResp memberWithdrawlResp = memberService.deleteMember(memberContext.getId(), jwtAccessToken);
        return new ResponseEntity<>(
                new RsData<>(true, "회원탈퇴 성공", memberWithdrawlResp),
                HttpStatus.OK);
    }
}
