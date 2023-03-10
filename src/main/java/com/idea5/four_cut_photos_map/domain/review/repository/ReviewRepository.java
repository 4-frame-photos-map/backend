package com.idea5.four_cut_photos_map.domain.review.repository;

import com.idea5.four_cut_photos_map.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByShopId(Long shopId);

    List<Review> findAllByShopIdOrderByCreateDateDesc(Long shopId);

    Optional<Review> findByWriterIdAndShopId(Long writerId, Long shopId);

    List<Review> findAllByWriterIdOrderByCreateDateDesc(Long writerId);

    List<Review> findTop3ByShopIdOrderByCreateDateDesc(Long shopId);
}
