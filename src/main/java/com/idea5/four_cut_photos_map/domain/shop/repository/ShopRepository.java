package com.idea5.four_cut_photos_map.domain.shop.repository;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByPlaceName(String placeName);

    Optional<List<Shop>> findByBrand(String keyword);

    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE shop AUTO_INCREMENT = 1", nativeQuery = true)
    void truncate(); // for test
}
