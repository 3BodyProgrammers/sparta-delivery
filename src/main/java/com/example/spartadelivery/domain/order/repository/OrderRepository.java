package com.example.spartadelivery.domain.order.repository;

import com.example.spartadelivery.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStoreIdIn(List<Long> storeIds, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Optional<Order> findWithStoreById(Long orderId);
}
