package com.example.spartadelivery.domain.order.repository;

import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findByStoreIdIn(List<Long> storeIds, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Optional<Order> findWithStoreById(Long orderId);

    @EntityGraph(attributePaths = {"store", "user"})
    Optional<Order> findWithStoreAndUserById(Long orderId);

    List<Order> findAllByStoreIdAndDeletedAtBetweenAndStatus(Long id, LocalDateTime start, LocalDateTime end, OrderStatus status);

}
