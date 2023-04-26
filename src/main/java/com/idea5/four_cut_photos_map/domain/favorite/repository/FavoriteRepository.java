package com.idea5.four_cut_photos_map.domain.favorite.repository;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByShopIdAndMemberId(Long shopId, Long memberId);
    List<Favorite> findByMemberIdOrderByCreateDateDesc(Long memberId);
    List<Favorite> findByMember(Member member);
    Long countByMember(Member member);
    List<Favorite> findByCreateDateAfter(LocalDateTime minusMonths);
    @Query("SELECT f.shop.id FROM Favorite f WHERE f.createDate BETWEEN :startDate AND :endDate GROUP BY f.shop.id HAVING COUNT(f.shop.id) >= 3")
    List<Long> findShopIdsWithMoreThanThreeFavorites(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
