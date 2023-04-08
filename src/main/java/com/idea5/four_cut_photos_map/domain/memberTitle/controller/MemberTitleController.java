package com.idea5.four_cut_photos_map.domain.memberTitle.controller;

import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
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

import java.util.List;

@RestController
@RequestMapping("/member-titles")
@RequiredArgsConstructor
public class MemberTitleController {
    private final MemberTitleService memberTitleService;

    // 회원 칭호 정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MemberTitleInfoResp> getMemberTitleInfo(@PathVariable Long id) {
        MemberTitleInfoResp memberTitleInfo = memberTitleService.getMemberTitleInfo(id);
        return ResponseEntity.ok(memberTitleInfo);
    }

    // 모든 칭호 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<List<MemberTitleResp>> getMemberTitles(@AuthenticationPrincipal MemberContext memberContext) {
        List<MemberTitleResp> memberTitles = memberTitleService.getMemberTitles(memberContext.getId());
        return ResponseEntity.ok(memberTitles);
    }
}
