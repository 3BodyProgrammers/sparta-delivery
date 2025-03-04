package com.example.spartadelivery.domain.order.repository;

import com.example.spartadelivery.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStoreIdIn(List<Long> storeIds, Pageable pageable);
}
