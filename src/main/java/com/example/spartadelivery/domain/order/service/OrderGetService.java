package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderGetService {

    private final OrderRepository orderRepository;

    public Order findOrderWithStoreById(Long orderId) {
        return orderRepository.findWithStoreById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
    }

    public List<Order> findAllByStoreIdAndDeletedAtBetweenAndStatus(Long id, LocalDateTime start, LocalDateTime end) {
        return orderRepository.findAllByStoreIdAndDeletedAtBetweenAndStatus(id, start, end, OrderStatus.COMPLETED);
    }

}
