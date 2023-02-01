package com.idea5.four_cut_photos_map.domain.title.service;

import com.idea5.four_cut_photos_map.domain.title.entity.Title;
import com.idea5.four_cut_photos_map.domain.title.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TitleService {
    private final TitleRepository titleRepository;

    public Title findById(Long id) {
        return titleRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("title 없음");
        });
    }
}
