package com.example.spartadelivery.domain.review.repository;

import com.example.spartadelivery.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderId(Long orderId);

    @EntityGraph(attributePaths = "store")
    Optional<Review> findReviewWithStoreById(Long id);

    @Query("""
        SELECT r FROM Review r
        WHERE r.store.id = :storeId
        AND r.deletedAt IS NULL
        AND (:minRating IS NULL OR r.rating >= :minRating)
        AND (:maxRating IS NULL OR r.rating <= :maxRating)
    """)
    @EntityGraph(attributePaths = {"user", "store", "order"})
    Page<Review> findAllByStoreIdAndRatingRange(@Param("storeId") Long storeId, @Param("minRating") Byte minRating, @Param("maxRating") Byte maxRating, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.store.id = :storeId AND r.deletedAt IS NULL")
    Long countReviewByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.id = :storeId AND r.deletedAt IS NULL")
    Double calculateAverageReviewScore(@Param("storeId") Long storeId);

}
