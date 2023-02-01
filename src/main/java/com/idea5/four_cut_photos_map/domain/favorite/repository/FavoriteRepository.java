package com.idea5.four_cut_photos_map.domain.favorite.repository;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
