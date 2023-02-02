package com.idea5.four_cut_photos_map.domain.title.repository;

import com.idea5.four_cut_photos_map.domain.title.entity.MemberTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<MemberTitle, Long> {
}
