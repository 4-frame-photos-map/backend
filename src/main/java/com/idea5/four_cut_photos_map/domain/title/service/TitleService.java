package com.idea5.four_cut_photos_map.domain.title.service;

import com.idea5.four_cut_photos_map.domain.title.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.title.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TitleService {
    private final TitleRepository titleRepository;

    public MemberTitle findById(Long id) {
        return titleRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("memberTitle 없음");
        });
    }
}
