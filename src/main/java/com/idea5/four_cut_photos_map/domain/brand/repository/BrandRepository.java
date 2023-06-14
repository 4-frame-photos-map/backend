package com.idea5.four_cut_photos_map.domain.brand.repository;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandName(String brandName);
}
