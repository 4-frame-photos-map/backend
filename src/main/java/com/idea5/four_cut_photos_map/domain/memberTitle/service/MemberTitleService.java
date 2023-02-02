package com.idea5.four_cut_photos_map.domain.memberTitle.service;

import com.idea5.four_cut_photos_map.domain.memberTitle.dto.response.MemberTitleInfoResp;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTitleService {
    private final MemberTitleRepository memberTitleRepository;

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
}
