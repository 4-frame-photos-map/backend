package com.idea5.four_cut_photos_map.domain.shoptitlelog.repository;

import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopTitleLogRepository extends JpaRepository<ShopTitleLog, Long> {

    Optional<List<ShopTitleLog>> findAllByShopId(Long shopId);
}
