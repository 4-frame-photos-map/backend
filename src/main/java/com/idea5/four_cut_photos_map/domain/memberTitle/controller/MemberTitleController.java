package com.idea5.four_cut_photos_map.domain.memberTitle.controller;

import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.service.MemberTitleService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member-title")
@RequiredArgsConstructor
public class MemberTitleController {
    private final MemberTitleService memberTitleService;

    // 회원 칭호 정보 조회
    @GetMapping("/info/{id}")
    public ResponseEntity<RsData> getMemberTitleInfo(@PathVariable Long id) {
        MemberTitleInfoResp memberTitleInfo = memberTitleService.getMemberTitleInfo(id);
        return new ResponseEntity<>(
                new RsData<>(true, "회원 칭호 정보 조회 성공", memberTitleInfo),
                HttpStatus.OK
        );
    }
}
