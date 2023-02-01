package com.idea5.four_cut_photos_map.domain.member.controller;

import com.idea5.four_cut_photos_map.domain.member.dto.response.MemberInfoResp;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    // 회원 기본정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ResponseEntity<RsData> getProfile(@AuthenticationPrincipal MemberContext memberContext) {
        MemberInfoResp memberInfoResp = memberService.getMemberInfo(memberContext.getId());
        RsData<MemberInfoResp> body = new RsData<>(
                true, "회원 정보 조회 성공", memberInfoResp
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
