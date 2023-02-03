package com.idea5.four_cut_photos_map.domain.favorite.repository;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByShopIdAndMemberId(Long shopId, Long memberId);
    void deleteByShopIdAndMemberId(Long shopId, Long memberId);
    Optional<List<Favorite>> findByMemberId(Long memberId);
}