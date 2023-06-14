package com.idea5.four_cut_photos_map.domain.shoptitle.repository;

import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopTitleRepository extends JpaRepository<ShopTitle, Long> {

}