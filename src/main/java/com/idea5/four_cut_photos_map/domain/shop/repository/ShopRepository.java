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
    @Query("SELECT s FROM Shop s " +
            "WHERE ((FUNCTION('REPLACE', s.placeName, ' ', '') = :placeName AND FUNCTION('REPLACE', s.address, ' ', '') LIKE %:roadAddress%)" +
            "OR (FUNCTION('REPLACE', s.placeName, ' ', '') = :placeName AND FUNCTION('REPLACE', s.address, ' ', '') LIKE %:address%)" +
            "OR  (FUNCTION('REPLACE', s.placeName, ' ', '') = :placeName) AND s.address IS NULL)")
    List<Shop> findByPlaceNameOrAddressIgnoringSpace(String placeName, String roadAddress, String address);

    Optional<Shop> findByPlaceName(String placeName);

    boolean existsByPlaceName(String placeName);
}