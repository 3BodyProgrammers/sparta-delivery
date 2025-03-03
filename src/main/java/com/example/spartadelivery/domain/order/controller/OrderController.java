package com.example.spartadelivery.domain.order.controller;

import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSaveResponseDto> save(
            Long userId, String userRole,
            @RequestBody OrderSaveRequestDto request
    ) {
        OrderSaveResponseDto response = orderService.save(userId, userRole, request);
        return ResponseEntity.ok(response);
    }
}
