package com.example.spartadelivery.domain.order.repository;

import com.example.spartadelivery.domain.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"store"})
    Optional<Order> findWithStoreById(Long orderId);
}
