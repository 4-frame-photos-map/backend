package com.idea5.four_cut_photos_map.domain.shop.repository;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByPlaceName(String keyword);

    Optional<List<Shop>> findByBrand(String keyword);
}
