package com.idea5.four_cut_photos_map.domain.shoptitlelog.repository;

import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopTitleLogRepository extends JpaRepository<ShopTitleLog, Long> {
    List<ShopTitleLog> findAllByShopId(Long shopId);

    Optional<ShopTitleLog> findByShopIdAndShopTitleId(Long shopId, Long shopTitleId);

    boolean existsByShopIdAndShopTitleId(Long shopId, Long shopTitleId);

    boolean existsByShopId(Long shopId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ShopTitle st WHERE st.createDate < :cutoffDate")
    void deleteOldShopTitles(@Param("oneMonthAgo") LocalDateTime cutoffDate);
}
