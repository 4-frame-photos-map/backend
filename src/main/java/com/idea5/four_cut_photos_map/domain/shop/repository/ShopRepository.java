package com.idea5.four_cut_photos_map.domain.shop.repository;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query(value = "SELECT * FROM shop " +
            "WHERE ((REPLACE(place_name, ' ', '') = :placeName AND REPLACE(address, ' ', '') LIKE CONCAT('%', :roadAddress, '%')) " +
            "OR (REPLACE(place_name, ' ', '') = :placeName AND REPLACE(address, ' ', '') LIKE CONCAT('%', :address, '%')) " +
            "OR (REPLACE(place_name, ' ', '') = :placeName AND address IS NULL)) " +
            "LIMIT 1", nativeQuery = true)
    Optional<Shop> findByPlaceNameOrAddressIgnoringSpace(String placeName, String roadAddress, String address);

    Optional<Shop> findByPlaceName(String placeName);

    boolean existsByPlaceName(String placeName);
}