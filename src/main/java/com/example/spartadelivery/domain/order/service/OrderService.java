package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;




    public Order findOrderWithStoreById(Long orderId) {
        return orderRepository.findWithStoreById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "주문을 찾을 수 없습니다."));
    }
}
