package com.idea5.four_cut_photos_map.domain.review.repository;

import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByShopId(Long shopId);

    List<Review> findAllByShopIdOrderByCreateDateDesc(Long shopId);

    Optional<Review> findByWriterIdAndShopId(Long writerId, Long shopId);

    List<Review> findAllByWriterIdOrderByCreateDateDesc(Long writerId);

    List<Review> findTop3ByShopIdOrderByCreateDateDesc(Long shopId);

    int countByShop(Shop shop);

    @Query("SELECT AVG(r.starRating) FROM Review r WHERE r.shop.id = :shopId")
    Double getAverageStarRating(@Param("shopId") Long shopId);

    Long countByWriter(Member writer);

    void deleteByWriterId(Long memberId);

    List<Review> findByModifyDateAfter(LocalDateTime minusMonths);

    @Query("SELECT r FROM Review r WHERE r.createDate BETWEEN :startDate AND :endDate AND (r.purity = 'GOOD' OR r.purity = 'BAD')")
    List<Review> findLastMonthReviewsWithGoodOrBadPurity(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}