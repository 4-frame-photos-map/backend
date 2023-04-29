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
    List<Shop> findDistinctByPlaceNameOrRoadAddressNameContaining(String brandName, String roadAddressName);
    Optional<Shop> findByPlaceName(String placeName);
    boolean existsByPlaceName(String placeName);
}