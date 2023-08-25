package com.idea5.four_cut_photos_map.domain.memberTitle.controller;

import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitlesResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member-titles")
@RequiredArgsConstructor
public class MemberTitleController {
    private final MemberTitleService memberTitleService;

    // 회원 칭호 단건 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MemberTitleResp> getMemberTitleInfo(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext) {
        MemberTitleResp memberTitleInfo = memberTitleService.getMemberTitle(id, memberContext.getMember());
        return ResponseEntity.ok(memberTitleInfo);
    }

    // 회원 칭호 전체 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<MemberTitlesResp> getMemberTitles(@AuthenticationPrincipal MemberContext memberContext) {
        MemberTitlesResp memberTitles = memberTitleService.getMemberTitles(memberContext.getMember());
        return ResponseEntity.ok(memberTitles);
    }
}
