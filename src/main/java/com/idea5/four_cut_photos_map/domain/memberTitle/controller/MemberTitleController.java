package com.idea5.four_cut_photos_map.domain.memberTitle.controller;

import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member-titles")
@RequiredArgsConstructor
public class MemberTitleController {
    private final MemberTitleService memberTitleService;

    // 회원 칭호 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<RsData> getMemberTitleInfo(@PathVariable Long id) {
        MemberTitleInfoResp memberTitleInfo = memberTitleService.getMemberTitleInfo(id);
        return new ResponseEntity<>(
                new RsData<>(true, "회원 칭호 정보 조회 성공", memberTitleInfo),
                HttpStatus.OK
        );
    }

    // 모든 칭호 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<RsData> getMemberTitles(@AuthenticationPrincipal MemberContext memberContext) {
        List<MemberTitleResp> memberTitles = memberTitleService.getMemberTitles(memberContext.getId());
        return new ResponseEntity<>(
                new RsData<>(true, "전체 칭호 조회 성공", memberTitles),
                HttpStatus.OK
        );
    }
}
