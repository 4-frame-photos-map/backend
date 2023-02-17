package com.idea5.four_cut_photos_map.domain.memberTitle.repository;

import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTitleRepository extends JpaRepository<MemberTitle, Long> {
    public List<MemberTitle> findAllByOrderByIdAsc();
}
