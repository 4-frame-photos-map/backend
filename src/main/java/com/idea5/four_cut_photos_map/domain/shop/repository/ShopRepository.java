package com.idea5.four_cut_photos_map.domain.shop.repository;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
